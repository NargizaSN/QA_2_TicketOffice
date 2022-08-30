import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface Connect {

    String url = "jdbc:postgresql://localhost:5432/";
    String user = "postgres";
    String password = "123";

    static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
//            System.out.println("Connected successfully to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}