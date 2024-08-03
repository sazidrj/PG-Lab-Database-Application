package Student;

import DatabaseConnection.CreateMySqlConnection;
import Model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentService {
    static CreateMySqlConnection createMySqlConnection = new CreateMySqlConnection();

    public Student findById(String id, Connection connection) throws SQLException {
        Student student = new Student();

        String query = "select * from student where id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();

        while(rs.next()){
            student.setId(id);
            student.setName(rs.getString("name"));
            student.setDob(rs.getDate("dob"));
            student.setCourse(rs.getString("program"));
            student.setBranch(rs.getString("branch"));
            student.setAddress(rs.getString("address"));
        }

        return student;
    }

    public ArrayList<OptedCourse> findOptedCourseBySem(String id, int sem, Connection connection) throws SQLException{
        ArrayList<OptedCourse> al = new ArrayList<>();

        String query = "select * from opted_courses where student_id = ? and sem = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, id);
        pstmt.setInt(2, sem);

        ResultSet rs = pstmt.executeQuery();

        while(rs.next()){

            OptedCourse optedCourse = new OptedCourse();
            optedCourse.setCourse_id(rs.getString("course_id"));
            optedCourse.setStudent_id(rs.getString("student_id"));
            optedCourse.setFaculty_id(rs.getString("faculty"));
            optedCourse.setCourse_name(rs.getString("course_name"));
            optedCourse.setCurrent_score(rs.getFloat("current_score"));
            optedCourse.setCredit_obtained(rs.getInt("credit_obtained"));
            optedCourse.setYear(rs.getInt("year"));
            optedCourse.setSem(rs.getInt("sem"));

            al.add(optedCourse);
        }

        return al;
    }

    public boolean isSubjectCodePresentInOptedCourse(String id, String sub_code, Connection connection) throws SQLException{
        String query = "select * from opted_courses where student_id = ? and course_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, id);
        pstmt.setString(2, sub_code);

        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){
            return true;
        }else{
            return false;
        }
    }

    public CourseOffered findCourseOfferedById(String id, Connection connection) throws SQLException{
        CourseOffered courseOffered = new CourseOffered();

        String query = "select * from offered_courses where course_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();

        while(rs.next()){
            courseOffered.setCourse_id(rs.getString("course_id"));
            courseOffered.setCgpa_constraint(rs.getInt("cgpa_constraint"));
            courseOffered.setSemester(rs.getInt("semester"));
            courseOffered.setAcademic_year(rs.getInt("academic_year"));
            courseOffered.setCredit(rs.getInt("credit"));
        }

        return courseOffered;
    }

    public CourseCatelog findCourseInfoByID(String course_id, Connection connection) throws SQLException{
          String query = "select * from course_catalog where course_id = ?";

          PreparedStatement statement = connection.prepareStatement(query);
          statement.setString(1, course_id);
          ResultSet resultSet = statement.executeQuery();

          CourseCatelog courseCatelog = new CourseCatelog();
          while(resultSet.next()){
              courseCatelog.setCourseId(course_id);
              courseCatelog.setCourseName(resultSet.getString("course_name"));
              courseCatelog.setPrerequisite(resultSet.getString("prerequisites"));
              courseCatelog.setSemester(resultSet.getInt("sem"));
              courseCatelog.setL(resultSet.getInt("L"));
              courseCatelog.setT(resultSet.getInt("T"));
              courseCatelog.setP(resultSet.getInt("P"));
              courseCatelog.setS(resultSet.getInt("S"));
              courseCatelog.setC(resultSet.getInt("C"));
              courseCatelog.setOffered(resultSet.getInt("offered"));
              courseCatelog.setAcademic_year(resultSet.getInt("year"));
          }

          return courseCatelog;
    }

}
