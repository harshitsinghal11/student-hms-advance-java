package com.hostelmanagement.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.hostelmanagement.database.DBConnection;

public class AdminSetup {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in);
             Connection con = DBConnection.getConnection()) {

            String checkSql =
                    "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";

            try (PreparedStatement check =
                    con.prepareStatement(checkSql);
                 ResultSet rs = check.executeQuery()) {

                rs.next();

                if (rs.getInt(1) > 0) {
                    System.out.println(
                            "An Admin account already exists."
                    );
                    return;
                }
            }

            System.out.print("Admin full name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Admin username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Admin password (minimum 12 characters): ");
            String password = scanner.nextLine();

            if (name.isEmpty() || name.length() > 100
                    || username.length() < 3
                    || username.length() > 50
                    || !username.matches("[A-Za-z0-9._-]+")
                    || password.length() < 12) {

                System.out.println("Invalid input. Admin not created.");
                return;
            }

            String sql = """
                    INSERT INTO users
                        (full_name, username, password_hash, role)
                    VALUES (?, ?, ?, 'ADMIN')
                    """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, username);
                ps.setString(3, PasswordUtil.hashPassword(password));

                ps.executeUpdate();

                System.out.println("Admin account created successfully.");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Admin setup failed: " + e.getMessage()
            );
        }
    }
}