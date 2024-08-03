package AcademicStaff;

import Constants.Constants;
import Model.CourseCatelog;
import Model.OptedCourse;
import Student.StudentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AcademicService {

    public ArrayList<CourseCatelog> getCourseCatalog(Connection connection) throws SQLException {
        String query = "SELECT * FROM course_catalog";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<CourseCatelog> availableCourses = new ArrayList<>();
        while (resultSet.next()) {
            CourseCatelog availableCourse = new CourseCatelog();
            availableCourse.setCourseId(resultSet.getString("course_id"));
            availableCourse.setCourseName(resultSet.getString("course_name"));
            availableCourse.setPrerequisite(resultSet.getString("prerequisites"));
            availableCourse.setSemester(Constants.SEM);
            availableCourse.setAcademic_year(Constants.YEAR);
            availableCourse.setL(resultSet.getInt("L"));
            availableCourse.setT(resultSet.getInt("T"));
            availableCourse.setP(resultSet.getInt("P"));
            availableCourse.setS(resultSet.getInt("S"));
            availableCourse.setC(resultSet.getInt("C"));
            availableCourse.setOffered(resultSet.getInt("offered"));
            availableCourses.add(availableCourse);
        }
        return availableCourses;
    }

    public int addCourse(CourseCatelog newCourse, Connection connection) throws SQLException {
        String query = "INSERT INTO course_catalog VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, newCourse.getCourseId());
        statement.setString(2, newCourse.getCourseName());
        statement.setString(3, newCourse.getPrerequisite());
        statement.setInt(4, newCourse.getL());
        statement.setInt(5, newCourse.getT());
        statement.setInt(6, newCourse.getP());
        statement.setInt(7, newCourse.getS());
        statement.setInt(8, newCourse.getC());
        statement.setInt(9, newCourse.getOffered());
        statement.setInt(10, newCourse.getSemester());
        statement.setInt(11, newCourse.getAcademic_year());
        return statement.executeUpdate();
    }

    public ArrayList<OptedCourse> findByStudentId(String studentId, Connection connection) throws SQLException {
        String query = "SELECT * FROM opted_courses WHERE student_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, studentId);
        ResultSet resultSet = statement.executeQuery();

        ArrayList<OptedCourse> al = new ArrayList<>();

        while(resultSet.next()){
            OptedCourse optedCourse = new OptedCourse();

            optedCourse.setCourse_id(resultSet.getString("course_id"));
            optedCourse.setFaculty_id(resultSet.getString("faculty"));
            optedCourse.setStudent_id(resultSet.getString("student_id"));
            optedCourse.setCourse_name(resultSet.getString("course_name"));
            optedCourse.setCurrent_score(resultSet.getFloat("current_score"));

            al.add(optedCourse);
        }
        return al;
    }

    public int deleteCourse(String courseId, Connection connection) throws SQLException {
        String query = "DELETE FROM course_catalog WHERE course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, courseId);
        return statement.executeUpdate();
    }


}
