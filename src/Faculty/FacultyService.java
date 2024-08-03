package Faculty;

import Constants.Constants;
import DatabaseConnection.CreateMySqlConnection;
import Model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;

public class FacultyService {
    static CreateMySqlConnection createMySqlConnection = new CreateMySqlConnection();

    public Faculty findById(String id, Connection connection) throws SQLException {
        Faculty faculty = new Faculty();

        String query = "select * from faculty where id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        ResultSet rs = statement.executeQuery();

        while(rs.next()){
            faculty.setId(rs.getString("id"));
            faculty.setName(rs.getString("name"));
            faculty.setDob(rs.getString("dob"));
            faculty.setDepartment(rs.getString("department"));
            faculty.setAddress(rs.getString("address"));
        }

        return faculty;
    }

    public ArrayList<CourseOffered> findAllCourseOffered(Connection connection) throws SQLException{
        ArrayList<CourseOffered> al = new ArrayList<>();

        String query = "select * from course_offered";
        ResultSet rs = createMySqlConnection.excuteQuery(query,connection);

        while(rs.next()){
            CourseOffered courseOffered = new CourseOffered();

            courseOffered.setCourse_id(rs.getString("course_id"));
            Faculty faculty = findById(rs.getString("id"), connection);
            courseOffered.setFaculty(faculty);

            courseOffered.setCgpa_constraint(rs.getInt("cgpa_constraint"));
            courseOffered.setSemester(rs.getInt("semester"));
            courseOffered.setAcademic_year(rs.getInt("academic_year"));

            al.add(courseOffered);
        }

        return al;
    }

    public CourseOffered findByCourseId(String course_id, Connection connection) throws SQLException{
        String query = "select * from course_offered where course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, course_id);
        ResultSet rs =  statement.executeQuery();

        CourseOffered courseOffered = new CourseOffered();

        while(rs.next()){
            courseOffered.setCourse_id(rs.getString("course_id"));
            Faculty faculty = findById(rs.getString("id"), connection);
            courseOffered.setFaculty(faculty);

            courseOffered.setCgpa_constraint(rs.getInt("cgpa_constraint"));
            courseOffered.setSemester(rs.getInt("semester"));
            courseOffered.setAcademic_year(rs.getInt("academic_year"));
        }

        return courseOffered;
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
            optedCourse.setCredit_obtained(resultSet.getInt("credit_obtained"));
            optedCourse.setSem(resultSet.getInt("sem"));
            optedCourse.setYear(resultSet.getInt("year"));
            al.add(optedCourse);
        }
        return al;
    }

    public ArrayList<CourseCatelog> getAvailableCoursesToOffer(Connection connection) throws SQLException {
        String query = "SELECT * FROM course_catalog WHERE offered = 0";
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

    public void offerCourse(Faculty facultyInfo, CourseCatelog courseToBeOffered, int cgpaConstraint, Connection connection) throws SQLException{

        String query = "UPDATE course_catalog SET offered = 1 WHERE course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, courseToBeOffered.getCourseId());
        statement.executeUpdate();

        query = "INSERT INTO offered_courses VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, courseToBeOffered.getCourseId());
        statement.setString(2, facultyInfo.getId());
        statement.setInt(3, cgpaConstraint);
        statement.setInt(4, Constants.SEM);
        statement.setInt(5, Constants.YEAR);
        statement.setInt(6, courseToBeOffered.getC());
        statement.executeUpdate();
    }

    public ArrayList<CourseOffered> getMyOfferedCourses(Faculty facultyInfo, Connection connection) throws SQLException {
        String query = "SELECT * FROM offered_courses WHERE faculty_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, facultyInfo.getId());
        ResultSet resultSet = statement.executeQuery();
        ArrayList<CourseOffered> offeredCourses = new ArrayList<>();
        while(resultSet.next()){
            CourseOffered courseOffered = new CourseOffered();
            courseOffered.setCourse_id(resultSet.getString("course_id"));
            courseOffered.setFaculty(facultyInfo);
            courseOffered.setCgpa_constraint(resultSet.getFloat("cgpa_constraint"));
            courseOffered.setSemester(resultSet.getInt("sem"));
            courseOffered.setAcademic_year(resultSet.getInt("year"));
            courseOffered.setCredit(resultSet.getInt("credit"));
            offeredCourses.add(courseOffered);
        }
        return offeredCourses;
    }

    public ArrayList<CourseOffered> getMyOfferedCoursesBySemester(int semester, Connection connection) throws SQLException {
        String query = "SELECT * FROM offered_courses WHERE sem = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, semester);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<CourseOffered> offeredCourses = new ArrayList<>();
        while(resultSet.next()){
            CourseOffered courseOffered = new CourseOffered();
            courseOffered.setCourse_id(resultSet.getString("course_id"));
            courseOffered.setCgpa_constraint(resultSet.getFloat("cgpa_constraint"));
            courseOffered.setSemester(resultSet.getInt("sem"));
            courseOffered.setAcademic_year(resultSet.getInt("year"));
            courseOffered.setCredit(resultSet.getInt("credit"));
            Faculty faculty = findById(resultSet.getString("faculty_id"), connection);
            courseOffered.setFaculty(faculty);
            offeredCourses.add(courseOffered);
        }
        return offeredCourses;
    }


    public ArrayList<ScoreRecord> readCSV(String filePath) throws Exception {
        String lineBy = "";
        String recordBy = ",";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ArrayList<ScoreRecord> scoreRecords = new ArrayList<>();
        while ((lineBy = reader.readLine()) != null){
            String[] scoreRecord = lineBy.split(recordBy);
            ScoreRecord scoreRecordObj = new ScoreRecord();
            scoreRecordObj.setCourseId(scoreRecord[0]);
            scoreRecordObj.setStudentId(scoreRecord[1]);
            scoreRecordObj.setScore(Float.parseFloat(scoreRecord[2]));
            scoreRecords.add(scoreRecordObj);
        }
        return scoreRecords;
    }

    public int uploadRecord(ScoreRecord scoreRecord, ArrayList<CourseOffered> offeredCourses, Connection connection) throws SQLException {
        String query = "UPDATE opted_courses SET current_score = ?, credit_obtained = ? WHERE course_id = ? AND student_id = ?";
        int credit = 0;
        for(CourseOffered courseOffered: offeredCourses){
            if(courseOffered.getCourse_id().equals(scoreRecord.getCourseId())){
                credit = courseOffered.getCredit();
                break;
            }
        }
        if(scoreRecord.getScore() < Constants.PASSING_MARKS){
            credit = 0;
        }
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setFloat(1, scoreRecord.getScore());
        statement.setInt(2, credit);
        statement.setString(3, scoreRecord.getCourseId());
        statement.setString(4, scoreRecord.getStudentId());
        return statement.executeUpdate();
    }

    public ArrayList<OptedCourse> getScoresOfAllFor(String courseId, Connection connection) throws SQLException {
        String query = "SELECT * FROM opted_courses WHERE course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, courseId);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<OptedCourse> scoresRecords = new ArrayList<>();
        while(resultSet.next()){
            OptedCourse scoreRecord = new OptedCourse();
            scoreRecord.setFaculty_id(resultSet.getString("faculty"));
            scoreRecord.setStudent_id(resultSet.getString("student_id"));
            scoreRecord.setCourse_id(resultSet.getString("course_id"));
            scoreRecord.setCurrent_score(resultSet.getFloat("current_score"));
            scoreRecord.setCourse_name(resultSet.getString("course_name"));
            scoreRecord.setCredit_obtained(resultSet.getInt("credit_obtained"));
            scoreRecord.setSem(resultSet.getInt("sem"));
            scoreRecord.setYear(resultSet.getInt("year"));
            scoresRecords.add(scoreRecord);
        }
        return scoresRecords;
    }

    public ArrayList<CourseOffered> getMyCurrentOfferedCourses(Faculty facultyInfo, Connection connection) throws SQLException {
        String query = "SELECT * FROM offered_courses WHERE faculty_id = ? AND sem = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, facultyInfo.getId());
        statement.setInt(2, Constants.SEM);
        ResultSet resultSet = statement.executeQuery();
        ArrayList<CourseOffered> offeredCourses = new ArrayList<>();
        while(resultSet.next()){
            CourseOffered courseOffered = new CourseOffered();
            courseOffered.setCourse_id(resultSet.getString("course_id"));
            courseOffered.setFaculty(facultyInfo);
            courseOffered.setCgpa_constraint(resultSet.getFloat("cgpa_constraint"));
            courseOffered.setSemester(resultSet.getInt("sem"));
            courseOffered.setAcademic_year(resultSet.getInt("year"));
            courseOffered.setCredit(resultSet.getInt("credit"));
            offeredCourses.add(courseOffered);
        }
        return offeredCourses;
    }

    public void withdrawCourse(CourseOffered courseToBeWithdrawn, Connection connection) throws SQLException{

        String query = "UPDATE course_catalog SET offered = 0 WHERE course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, courseToBeWithdrawn.getCourse_id());
        statement.executeUpdate();

        query = "DELETE FROM offered_courses WHERE course_id = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, courseToBeWithdrawn.getCourse_id());
        statement.executeUpdate();
    }
}