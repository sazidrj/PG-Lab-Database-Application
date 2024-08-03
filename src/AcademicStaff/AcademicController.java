package AcademicStaff;

import Constants.Constants;
import Model.*;
import Student.StudentService;
import Faculty.FacultyService;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class AcademicController {
    private static Scanner sc = new Scanner(System.in);
    private static InputStreamReader r = new InputStreamReader(System.in);
    private static BufferedReader br = new BufferedReader(r);
    private static AcademicService academicServices = new AcademicService();
    StudentService studentService = new StudentService();
    FacultyService facultyService = new FacultyService();

    private void createCourse(Connection connection) throws Exception {
        ArrayList<CourseCatelog> courseCatalog = academicServices.getCourseCatalog(connection);
        ArrayList<String> existingCourses = new ArrayList<>();
        for (CourseCatelog courseCatelog : courseCatalog) {
            existingCourses.add(courseCatelog.getCourseId());
        }
        System.out.print("Enter Course ID: ");
        String courseId = sc.next();
        courseId = courseId.toUpperCase();
        if (existingCourses.contains(courseId)) {
            System.out.println(courseId + " is already created.");
            return;
        }
        System.out.print("Enter Course Name: ");
        String courseName = br.readLine();
        System.out.print("Enter Course ID(s) of Prerequisites (Enter NA if none): ");
        String prerequisites = br.readLine();
        prerequisites = prerequisites.toUpperCase();
        System.out.print("Enter L component: ");
        int l = sc.nextInt();
        System.out.print("Enter T component: ");
        int t = sc.nextInt();
        System.out.print("Enter P component: ");
        int p = sc.nextInt();
        System.out.print("Enter S component: ");
        int s = sc.nextInt();
        System.out.print("Enter C component: ");
        int c = sc.nextInt();

        CourseCatelog newCourse = new CourseCatelog();
        newCourse.setCourseId(courseId);
        newCourse.setCourseName(courseName);
        newCourse.setPrerequisite(prerequisites);
        newCourse.setL(l);
        newCourse.setT(t);
        newCourse.setP(p);
        newCourse.setS(s);
        newCourse.setC(c);
        newCourse.setOffered(0);
        newCourse.setSemester(Constants.SEM);
        newCourse.setAcademic_year(Constants.YEAR);

        int done = academicServices.addCourse(newCourse, connection);

        if (done == 0) {
            System.out.println("Something went wrong");
        } else {
            System.out.println(courseId + " is added to Course Catalog.");
        }
    }

    private void viewGrade(Connection connection) throws SQLException {
        System.out.print("Enter Student ID: ");
        String studentId;
        studentId = sc.next();
        ArrayList<OptedCourse> optedCourses = academicServices.findByStudentId(studentId, connection);
        if (optedCourses.size() == 0) {
            System.out.println("This student is Not Registered in any course.");
        } else {
            System.out.println(
                    "Course ID" + "\t"
                            + "Student ID" + "\t\t"
                            + "Current Score" + "\t"
                            + "Course Name"
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

    private void viewCourseCatalog(Connection connection) throws SQLException {
        ArrayList<CourseCatelog> courseCatalog = academicServices.getCourseCatalog(connection);
        if (courseCatalog.size() == 0) {
            System.out.println("No courses in Course Catalog.");
            return;
        }
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
        for (CourseCatelog courseCatelog : courseCatalog) {
            System.out.println(
                    courseCatelog.getCourseId() + "\t\t" +
                            courseCatelog.getL() + "\t" +
                            courseCatelog.getT() + "\t" +
                            courseCatelog.getP() + "\t" +
                            courseCatelog.getS() + "\t" +
                            courseCatelog.getC() + "\t" +
                            courseCatelog.getCourseName() + "\t\t\t\t\t\t" +
                            courseCatelog.getPrerequisite()
            );
        }
    }


    public void generateTranscript(int semester, Connection connection) throws Exception {
        System.out.println("Enter Student ID : ");
        String studentId = br.readLine();;

        Student student = studentService.findById(studentId, connection);

        StringBuilder stb = new StringBuilder();

        stb.append("================================================= Student Details =========================================== \n");
        stb.append("Entry Number = ").append(student.getId());
        stb.append("\nName : ").append(student.getName());
        stb.append("\nProgram : ").append(student.getCourse());
        stb.append("\nBranch : ").append(student.getBranch());
        stb.append("\nAddress : ").append(student.getAddress());


        for (int i = 1; i < semester; i++) {
            ArrayList<OptedCourse> al = studentService.findOptedCourseBySem(studentId, i, connection);
            stb.append("\n-------------------------SEMESTER").append(i).append("----------------------");

            stb.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            stb.append("\n Subject_Code  Subject_Title   Faculty_Name   Marks_Scored  Credit_Obtained ");
            stb.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            for (OptedCourse optedCourse : al) {
                stb.append(optedCourse.getCourse_id()).append("   ");
                stb.append(optedCourse.getCourse_name()).append("   ");
                Faculty faculty = facultyService.findById(optedCourse.getFaculty_id(), connection);
                stb.append(faculty.getName()).append("   ");
                stb.append(optedCourse.getCurrent_score()).append("   ");
                stb.append(optedCourse.getCredit_obtained()).append("\n");
                stb.append("----------------------------------------------------------------------------------\n");
            }
        }

        try {
            FileWriter fileWriter = new FileWriter("transcript.txt");
            fileWriter.write(stb.toString());
            fileWriter.close();
            System.out.println("Transcript Created Successfully and saved at below path");
            System.out.println(System.getProperty("user.dir") + "\transcript.txt");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void deleteCourse(Connection connection) throws SQLException {
        ArrayList<CourseOffered> offeredCourses = facultyService.findAllCourseOffered(connection);
        ArrayList<String> offeredCourseCodes = new ArrayList<>();
        for (CourseOffered offeredCourse : offeredCourses) {
            offeredCourseCodes.add(offeredCourse.getCourse_id());
        }
        ArrayList<CourseCatelog> allCourses = academicServices.getCourseCatalog(connection);
        ArrayList<String> courseCodes = new ArrayList<>();
        for (CourseCatelog course : allCourses) {
            courseCodes.add(course.getCourseId());
        }
        if (courseCodes.size() == 0) {
            System.out.println("No Courses in Course Catalog.");
            return;
        }
        System.out.print("Enter Course ID to be deleted: ");
        String courseId = sc.next().toUpperCase().trim();
        if (!courseCodes.contains(courseId)) {
            System.out.println("Invalid Course ID was entered");
            return;
        }
        if (offeredCourseCodes.contains(courseId)) {
            System.out.println("The Course is already started. Hence, cannot be deleted.");
            return;
        }
        int done = academicServices.deleteCourse(courseId, connection);
        if (done == 0) {
            System.out.println("Something went wrong.");
        } else {
            System.out.println(courseId + " is removed from Course Catalog");
        }
    }

    static void logout(String username, Connection connection) throws SQLException{
        String query = "DELETE FROM login where username = ?";
        PreparedStatement pstms = connection.prepareStatement(query);
        pstms.setString(1, username);

        pstms.executeUpdate();
    }


    public void academicMain(int semester, String id, Connection connection) throws Exception {
        boolean run = true;
        while (run) {
            System.out.println("Welcome \n" +
                    "1. Create Course\n" +
                    "2. View Course Catalog\n" +
                    "3. View Grades of a student\n" +
                    "4. Generate Transcript of a student\n" +
                    "5. Delete a Course\n" +
                    "6. Logout");

            int input = sc.nextInt();

            switch (input) {
                case 1:
                    createCourse(connection);
                    break;
                case 2:
                    viewCourseCatalog(connection);
                    break;
                case 3:
                    viewGrade(connection);
                    break;
                case 4:generateTranscript(semester, connection);
                    break;
                case 5:
                    deleteCourse(connection);
                    break;
                case 6:logout(id, connection);
                    run = false;
                    break;
                default:
                    System.out.println("Invalid choice entered.");
                    break;
            }
        }
    }
}
