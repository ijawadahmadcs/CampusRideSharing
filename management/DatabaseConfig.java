// # Database connection
import java.sql.*;

public class DatabaseConfig {
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/ridesharedb";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (PASSWORD == null || PASSWORD.isEmpty()) {
                System.err.println("DB_PASSWORD is not set. Refusing to connect to the database. Set DB_PASSWORD in the environment.");
                return null;
            }
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    // Return a new independent Connection (useful for per-transaction work)
    public static Connection getNewConnection() {
        try {
            if (PASSWORD == null || PASSWORD.isEmpty()) {
                System.err.println("DB_PASSWORD is not set. Refusing to create a new DB connection.");
                return null;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return null;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}