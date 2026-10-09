package com.hostelmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hostel_db";

    private static final String DB_USER = "root";

    // Replace with your actual local MySQL password.
    private static final String DB_PASSWORD = "0301";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                DB_USER,
                DB_PASSWORD
        );
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("MySQL connected successfully!");
        } catch (SQLException e) {
            System.err.println("MySQL connection failed.");
            e.printStackTrace();
        }
    }
}