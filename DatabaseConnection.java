package com.studentdbms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager using JDBC.
 * Handles connection lifecycle for the Student DBMS.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/student_dbms?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";       // Change as needed
    private static final String PASSWORD = "your_password"; // Change as needed

    private static Connection connection = null;

    // Private constructor - Singleton pattern
    private DatabaseConnection() {}

    /**
     * Returns the singleton Connection instance.
     * Creates it if it doesn't exist or has been closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("  [DB] Connected to MySQL successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add connector JAR to classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the connection gracefully.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("  [DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("  [DB] Error closing connection: " + e.getMessage());
            }
        }
    }
}
