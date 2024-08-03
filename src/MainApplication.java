import AcademicStaff.AcademicController;
import DatabaseConnection.CreateMySqlConnection;
import Faculty.FacultyController;
import Student.StudentController;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class MainApplication {
    final static int semester = 3, year = 2022;
    static Scanner sc = new Scanner(System.in);

    public static void clearConsole(){
        try{
            if(System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }else{
                System.out.print("\033\143");
            }
        }catch(IOException | InterruptedException ex){
            System.out.println("error");
        }
    }

    static void login(String username, String password, int role, Connection connection) {
        String query = "INSERT INTO login VALUES(?,?,?)";
        try{
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, role);

            pstmt.executeUpdate();
        }catch(SQLException e){
            System.out.println("User already Logged In");
        }

    }

    static boolean authenticateUser(String username, String password, int role, Connection connection) throws SQLException{
        String query;

        if(role == 1){
            query = "select * from student where id = ? and password = ?";
        }else if(role == 2){
            query = "select * from faculty where id = ? and password = ?";
        }else{
            query = "select * from academic_office where id = ? and password = ?";
        }

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, password);

        ResultSet rs = pstmt.executeQuery();

        if(!rs.next()){
            System.out.println("Invalid Credentials");
            return false;
        }
        login(username,password,role, connection);

        return true;
    }

    public static void main(String arg[]) throws Exception{

        CreateMySqlConnection myConnection = new CreateMySqlConnection();
        Connection connection = myConnection.createConnection();
        boolean run = true;
        while(run){

            clearConsole();

            System.out.println(" SEMESTER "+semester + " Academic Year " + year+"\n"+
                    "Login As\n"+
                    "1. Student\n"+
                    "2. Faculty\n"+
                    "3. Academic Staff\n"+
                    "4. Exit");


            int role = Integer.parseInt(sc.nextLine());

            if(role < 1 || role > 4){
                System.out.println("Invalid Input");
                continue;
            }

            String username = "", password = "";

            if(role < 4){
                System.out.println("Enter Username : ");
                username = sc.nextLine();

                System.out.println("Enter password : ");
                password = sc.nextLine();

                boolean authenticated_user = authenticateUser(username, password, role, connection);

                if(!authenticated_user){
                    continue;
                }

            }

            switch(role){
                case 1:
                    StudentController studentController = new StudentController();
                    studentController.studentMain(username, connection, semester, year);
                    break;
                case 2:
                    FacultyController facultyController = new FacultyController();
                    facultyController.facultyMain(username, connection);
                    break;
                case 3:
                    AcademicController academicController = new AcademicController();
                    academicController.academicMain(semester, username,connection);
                    break;
                case 4:
                    run = false;
                    break;
                default:
                    System.out.println("Please enter valid input");
                    break;
            }

        }

    }

}
