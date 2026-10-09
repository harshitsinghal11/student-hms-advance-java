package com.hostelmanagement.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.User;

public class AuthService {

    public static User login(
            String username,
            String password
    ) throws SQLException {

        String sql = """
                SELECT user_id, full_name, username,
                       password_hash, role
                FROM users
                WHERE username = ?
                  AND is_active = TRUE
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    if (PasswordUtil.verifyPassword(
                            password, storedHash)) {

                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("full_name"),
                                rs.getString("username"),
                                rs.getString("role")
                        );
                    }
                }
            }
        }

        return null;
    }

    public static boolean registerStudent(
            String fullName,
            String username,
            String password
    ) throws SQLException {

        String sql = """
                INSERT INTO users
                    (full_name, username, password_hash, role)
                VALUES (?, ?, ?, 'STUDENT')
                """;

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName.trim());
            ps.setString(2, username.trim());
            ps.setString(3, hashedPassword);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            // MySQL error 1062 means duplicate unique value.
            if (e.getErrorCode() == 1062
                    && "23000".equals(e.getSQLState())) {
                return false;
            }

            throw e;
        }
    }
}