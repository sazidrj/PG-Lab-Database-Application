package Student;

import DatabaseConnection.CreateMySqlConnection;
import Faculty.FacultyService;
import Model.CourseCatelog;
import Model.CourseOffered;
import Model.OptedCourse;
import Model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentController {
    static Scanner sc = new Scanner(System.in);
    StudentService studentService = new StudentService();
    FacultyService facultyService = new FacultyService();

    static CreateMySqlConnection createMySqlConnection = new CreateMySqlConnection();

    void viewGrades(String id, int semester, int year, Connection connection) throws SQLException{

        Student studentById = studentService.findById(id, connection);


        for(int i = 1; i<semester; i++){
            System.out.println("SEMESTER " + i + "\n");
            ArrayList<OptedCourse> optedCourses = studentService.findOptedCourseBySem(id, i,connection);
            StringBuilder stb = new StringBuilder("");
            stb.append("Course Id\t").append("Course Name \t\t\t").append("Faculty Id\t").append("Current Score\t").append("Credit Obtained\n");
            for(OptedCourse optedCourse : optedCourses){
                stb.append(optedCourse.getCourse_id()).append("\t");
                stb.append(optedCourse.getCourse_name()).append("\t\t");
                stb.append(optedCourse.getFaculty_id()).append("\t");
                stb.append(optedCourse.getCurrent_score()).append("\t");
                stb.append(optedCourse.getCredit_obtained()).append("\n");
            }

            System.out.println(stb);
        }
    }


     void registerForCourse(String student_id, Connection connection, int semester, int year, int total_credits_allowed) throws SQLException{
        System.out.println();

        ArrayList<CourseOffered> al = facultyService.getMyOfferedCoursesBySemester(semester,connection);

        for(CourseOffered co : al){
            System.out.println(co.toString());
        }

         // check whether input subcode is correct or not

         boolean isInputCorrect = false;
         String subCode = null;

         while(!isInputCorrect) {
             System.out.println("Enter the subject code : ");
             subCode = sc.nextLine();

             for(CourseOffered co : al){
                 if(subCode.equals(co.getCourse_id())){
                     isInputCorrect = true;
                     break;
                 }
             }

             if(!isInputCorrect) {
                 System.out.println("Please! Enter Correct Subject Code...");
             }else{
                 isInputCorrect = true;
             }
         }

         // If previously done this course - check
         boolean isAlreadyOpted = studentService.isSubjectCodePresentInOptedCourse(student_id, subCode, connection);

         if(isAlreadyOpted){
            System.out.println("You can not enrolled the same course again.....");
            return;
        }

        // Prerequisites Check

         CourseCatelog courseCatelog = studentService.findCourseInfoByID(subCode, connection);

         String[] prerequisites_courses = courseCatelog.getPrerequisite().split(" ");

         for(String course : prerequisites_courses){
             boolean previouslyDoneCourse = studentService.isSubjectCodePresentInOptedCourse(student_id, course, connection);

             if(!previouslyDoneCourse){
                 System.out.println("Sorry!! You can not enrolled in this course. You have to do Prerequisites courses first.");
                 for(String pre_course : prerequisites_courses)
                     System.out.print(pre_course + ",");

                 System.out.println();

                 return;
             }
         }

         // Credit obtained in the previous semester

         ArrayList<OptedCourse> coursesDoneInPreviousSemester = studentService.findOptedCourseBySem(student_id, semester-1, connection);

         int creditObtainedInPreviousSem = 0;

         for(OptedCourse optedCourse : coursesDoneInPreviousSemester){
             creditObtainedInPreviousSem += optedCourse.getCredit_obtained();
         }

         int total_credits_so_far = 0;
         for(int i = 1; i<=semester; i++){
             ArrayList<OptedCourse> coursesDoneInSem = studentService.findOptedCourseBySem(student_id, i, connection);

             for(OptedCourse optedCourse : coursesDoneInSem){
                 total_credits_so_far += optedCourse.getCredit_obtained();
             }
         }

         total_credits_allowed = total_credits_allowed - total_credits_so_far;

         total_credits_allowed = Math.min(total_credits_allowed, (int)(1.25*creditObtainedInPreviousSem));

         if(courseCatelog.getC() > total_credits_allowed){
             System.out.println("Sorry!! You can not register for this course because of credit limit");
             return;
         }

         double student_cgpa = computeCGPA(student_id, semester, connection);

         CourseOffered courseOfferdById = facultyService.findByCourseId(subCode, connection);

        if(student_cgpa < courseOfferdById.getCgpa_constraint()){
            System.out.println("Sorry, You can not enroll in this course because of CGPA constraint");
        }else{
            String insertQuery = "INSERT INTO opted_course VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pstms = connection.prepareStatement(insertQuery);
            pstms.setString(1, subCode);
            pstms.setString(2, student_id);
            pstms.setString(3, courseCatelog.getCourseName());
            pstms.setString(4, courseOfferdById.getFaculty().getId());
            pstms.setInt(5, 0);
            pstms.setInt(6,0);
            pstms.setInt(7,courseOfferdById.getSemester());
            pstms.setInt(8, courseOfferdById.getAcademic_year());

            pstms.executeUpdate();
        }
    }


    void deregisterForCourse(int semester, Connection connection) throws SQLException{
        System.out.println("Enter the subject code : ");
        String subCode = sc.nextLine();

        String query = "DELETE FROM opted_course where course_id = ? and sem = ?";
        PreparedStatement pstms = connection.prepareStatement(query);
        pstms.setString(1, subCode);
        pstms.setInt(2, semester);

        int deletedRows = pstms.executeUpdate();

        if(deletedRows == 0){
            System.out.println("Something Went Wrong!!");
        }else{
            System.out.println("Course Unregistered Successfully");
        }
    }


    double computeCGPA(String student_id, int semester, Connection connection) throws SQLException{

        ArrayList<OptedCourse> optedCourseList = new ArrayList<>();

        int marksObtained = 0, totalMarks = 0;

        for(int i = 1; i<semester; i++){
            ArrayList<OptedCourse> coursesDoneInSem = studentService.findOptedCourseBySem(student_id, i, connection);

            for(OptedCourse optedCourse : coursesDoneInSem){
                marksObtained += optedCourse.getCurrent_score();
                totalMarks += 100;
            }
        }

        double total_cgpa = ((1.0*marksObtained/totalMarks)*100)/9.5;

        System.out.printf("CURRENT CGPA = %.2\n", total_cgpa);

        return total_cgpa;
    }


    void logout(String id, Connection connection) throws SQLException{
        String query = "DELETE FROM login where username = ?";
        PreparedStatement pstms = connection.prepareStatement(query);
        pstms.setString(1, id);

        pstms.executeUpdate();
    }

    public void studentMain(String id, Connection connection, int semester, int year) throws SQLException {
        boolean flag =  true;

        while(flag){
            System.out.println("Welcome  \n" +
                    "1.View Grades\n" +
                    "2.Register for course \n" +
                    "3.Deregister for course \n" +
                    "4.Compute CGPA \n" +
                    "5.Logout");

            int input = Integer.parseInt(sc.nextLine());

            switch(input){
                case 1:
                    viewGrades(id, semester, year, connection);
                    break;
                case 2:
                    registerForCourse(id, connection, semester, year, 32);
                    break;
                case 3:
                    deregisterForCourse(semester, connection);
                    break;
                case 4:
                    computeCGPA(id, semester, connection);
                    break;
                case 5:
                    logout(id,connection);
                    flag = false;
                    break;
                default:
                    System.out.println("Entered Invalid Choice....");
                    break;
            }
        }
    }



}
