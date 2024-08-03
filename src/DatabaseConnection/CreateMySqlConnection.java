package DatabaseConnection;
import java.sql.*;

public class CreateMySqlConnection {

   public Connection createConnection() throws SQLException {

        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "Sh@lsadilzief52");

        return connection;
    }

    public ResultSet excuteQuery(String query, Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        return rs;
    }


    public void closeConection(Connection connection) throws SQLException{
        connection.close();
    }
}
