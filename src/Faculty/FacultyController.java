package Faculty;

import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class FacultyController {
    static Scanner sc = new Scanner(System.in);
    static FacultyService facultyService = new FacultyService();
    static Faculty facultyInfo;
    static void viewGrade(Connection connection) throws Exception{
        System.out.print("Enter Student ID: ");
        String studentId;
        studentId = sc.next();
        ArrayList<OptedCourse> optedCourses = facultyService.findByStudentId(studentId, connection);
        if(optedCourses.size() == 0){
            System.out.println("This student is Not Registered in any course.");
        }else{
            System.out.println(
                "Course ID"+"\t"
                +"Student ID"+"\t\t"
                +"Current Score"+"\t"
                +"Course Name"
            );
            for (OptedCourse optedCourse : optedCourses) {
                System.out.println(
                        optedCourse.getCourse_id() + "\t\t"
                        + optedCourse.getStudent_id() + "\t\t"
                        + optedCourse.getCurrent_score() + "\t\t\t"
                        + optedCourse.getCourse_name()
                );
            }
        }
    }

    static void offerCourse(Connection connection) throws SQLException {
        ArrayList<CourseCatelog> availableCourses = facultyService.getAvailableCoursesToOffer(connection);
        if(availableCourses.size() == 0){
            System.out.println("No courses to offer.");
            return;
        }
        System.out.println("Available courses to offer: ");
        System.out.println(
                "Course ID\t" +
                "L\t" +
                "T\t" +
                "P\t" +
                "S\t" +
                "C\t" +
                "Course Name\t\t\t\t\t\t\t\t" +
                "Prerequisites"
        );
        for (CourseCatelog availableCourse : availableCourses) {
            System.out.println(
                    availableCourse.getCourseId() + "\t\t" +
                    availableCourse.getL() + "\t" +
                    availableCourse.getT() + "\t" +
                    availableCourse.getP() + "\t" +
                    availableCourse.getS() + "\t" +
                    availableCourse.getC() + "\t" +
                    availableCourse.getCourseName() + "\t\t\t\t\t\t" +
                    availableCourse.getPrerequisite()
            );
        }
        System.out.print("Enter Course ID you want to offer: ");
        String courseId = sc.next().toUpperCase();
        CourseCatelog courseToBeOffered = new CourseCatelog();
        boolean found = false;
        for (CourseCatelog availableCourse : availableCourses) {
            if (availableCourse.getCourseId().equals(courseId)) {
                courseToBeOffered = availableCourse;
                found = true;
                break;
            }
        }
        if (found) {
            System.out.print("Enter CGPA constraint for this course (Enter 0 if not applicable): ");
            int cgpaConstraint = sc.nextInt();
            facultyService.offerCourse(facultyInfo, courseToBeOffered, cgpaConstraint, connection);
            System.out.println("You have successfully floated course: " + courseId);
        } else {
            System.out.println("Invalid Course ID was Entered.");
        }
    }

    static void enterGrades(Connection connection) throws Exception {
        ArrayList<CourseOffered> offeredCourses = facultyService.getMyOfferedCourses(facultyInfo, connection);
        if(offeredCourses.size() == 0){
            System.out.println("You have not floated any course yet.");
            return;
        }
        System.out.print("Paste path of .csv file and press Enter: ");
        String filePath = sc.next();
        ArrayList<ScoreRecord> scoreRecords = facultyService.readCSV(filePath);
        ArrayList<String> offeredCourseCodes = new ArrayList<>();
        for (CourseOffered offeredCourse : offeredCourses) {
            offeredCourseCodes.add(offeredCourse.getCourse_id());
        }
        ArrayList<ScoreRecord> notUploaded = new ArrayList<>();
        for (ScoreRecord scoreRecord : scoreRecords) {
            if (offeredCourseCodes.contains(scoreRecord.getCourseId())) {
                int uploaded = facultyService.uploadRecord(scoreRecord, offeredCourses, connection);
                if (uploaded == 0) {
                    notUploaded.add(scoreRecord);
                }
            } else {
                notUploaded.add(scoreRecord);
            }
        }


        if(notUploaded.size() == scoreRecords.size()){
            System.out.println("No Records were uploaded because of one or more of the following reasons: \n" +
                    "1. Incorrect Student IDs\n" +
                    "2. No Students registered for your course\n" +
                    "3. Unauthorized Course ID");
        }else if(notUploaded.size() > 0){
            System.out.println("All records were uploaded except the following invalid records:");
            for (ScoreRecord scoreRecord : notUploaded) {
                System.out.println(
                        scoreRecord.getCourseId() + "\t" +
                        scoreRecord.getStudentId() + "\t" +
                        scoreRecord.getScore());
            }
        }else{
            System.out.println("All records were uploaded successfully");
        }
    }

    public static void myOfferedCourse(Connection connection) throws SQLException {
        ArrayList<CourseOffered> offeredCourses = facultyService.getMyOfferedCourses(facultyInfo, connection);
        if(offeredCourses.size() == 0){
            System.out.println("You have not floated any course yet.");
            return;
        }
        System.out.println(
                "Course ID\t" +
                "CGPA Constraint\t\t" +
                "Semester\t" +
                "Year\t" +
                "Credits");
        for (CourseOffered offeredCourse : offeredCourses) {
            System.out.println(
                    offeredCourse.getCourse_id() + "\t\t" +
                    offeredCourse.getCgpa_constraint() + "\t\t\t\t\t" +
                    offeredCourse.getSemester() + "\t\t\t" +
                    offeredCourse.getAcademic_year() + "\t" +
                    offeredCourse.getCredit() + "\t");
        }
    }

    static void viewGradesOfAll(Connection connection) throws Exception {
        ArrayList<CourseOffered> offeredCourses = facultyService.getMyOfferedCourses(facultyInfo, connection);
        ArrayList<String> offeredCourseCodes = new ArrayList<>();
        for (CourseOffered offeredCourse : offeredCourses) {
            offeredCourseCodes.add(offeredCourse.getCourse_id());
        }
        if(offeredCourses.size() == 0){
            System.out.println("You have not floated any course yet.");
            return;
        }
        System.out.print("Enter Course ID: ");
        String courseId = sc.next().toUpperCase().trim();
        if(!offeredCourseCodes.contains(courseId)){
            System.out.println(courseId + " either does not exists or is not offered by you.");
            return;
        }

        ArrayList<OptedCourse> courseScores = facultyService.getScoresOfAllFor(courseId, connection);

        if(courseScores.size() == 0){
            System.out.println("No Student is registered for your course: " + courseId);
            return;
        }
        System.out.println("Current Scores of all the registered students for your course: " + courseId + " (" + courseScores.get(0).getCourse_name() + ")");
        System.out.println(
                        "Student ID\t\t" +
                        "Current Score"
        );
        for(OptedCourse courseScore : courseScores){
            System.out.println(courseScore.getStudent_id() + "\t\t" +
                    courseScore.getCurrent_score());
        }
    }

    private static void withdrawCourse(Connection connection) throws SQLException {
        ArrayList<CourseOffered> myOfferedCourses = facultyService.getMyCurrentOfferedCourses(facultyInfo, connection);
        if(myOfferedCourses.size() == 0){
            System.out.println("You have not floated any course yet");
            return;
        }
        System.out.println("Available courses to withdraw: ");
        System.out.println(
                "Course ID\t" +
                        "CGPA Constraint\t" +
                        "Credits\t" +
                        "Semester\t" +
                        "Academic Year"
        );
        for (CourseOffered courseOffered : myOfferedCourses) {
            System.out.println(
                    courseOffered.getCourse_id() + "\t" +
                            courseOffered.getCgpa_constraint() + "\t\t\t" +
                            courseOffered.getCredit() + "\t" +
                            courseOffered.getSemester() + "\t\t" +
                            courseOffered.getAcademic_year()
            );
        }
        System.out.print("Enter Course ID you want to withdraw: ");
        String courseId = sc.next().toUpperCase();
        CourseOffered courseToBeWithdrawn = new CourseOffered();
        boolean found = false;
        for (CourseOffered courseOffered : myOfferedCourses) {
            if (courseOffered.getCourse_id().equals(courseId)) {
                courseToBeWithdrawn = courseOffered;
                found = true;
                break;
            }
        }
        if (found) {
            facultyService.withdrawCourse(courseToBeWithdrawn,connection);
            System.out.println("You have successfully withdrawn a course: " + courseId);
        } else {
            System.out.println("Invalid Course ID was Entered.");
        }
    }

    static void logout(String username, Connection connection) throws SQLException{
        String query = "DELETE FROM login where username = ?";
        PreparedStatement pstms = connection.prepareStatement(query);
        pstms.setString(1, username);

        pstms.executeUpdate();
    }

    public void facultyMain(String username, Connection connection) throws Exception {

        boolean run = true;
        facultyInfo = facultyService.findById(username, connection);
        while(run) {
            System.out.println("Welcome \n" +
                    "1.View Academic History of a Student\n" +
                    "2.Float a Course\n" +
                    "3.Enter Grades \n" +
                    "4.View Offered Course\n" +
                    "5.View grades of all students for my course\n" +
                    "6.Withdraw a Course\n" +
                    "7.Logout");

            int input = sc.nextInt();

            switch (input) {
                case 1:
                    FacultyController.viewGrade(connection);
                    break;
                case 2:
                    FacultyController.offerCourse(connection);
                    break;
                case 3:
                    FacultyController.enterGrades(connection);
                    break;
                case 4:
                    FacultyController.myOfferedCourse(connection);
                    break;
                case 5:
                    FacultyController.viewGradesOfAll(connection);
                    break;
                case 6:
                    FacultyController.withdrawCourse(connection);
                    break;
                case 7:
                    logout(username,connection);
                    run = false;
                    break;
                default:
                    System.out.println("Entered Invalid Choice.....");
                    break;
            }
        }
    }

}
