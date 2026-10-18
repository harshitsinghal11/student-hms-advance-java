package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.auth.PasswordUtil;
import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.Student;

public class StudentDAO {

    public static List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = """
                SELECT s.student_id, s.user_id, u.full_name, u.username,
                       s.roll_number, s.course, s.contact_number,
                       s.emergency_contact, s.address
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                ORDER BY s.student_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(extractStudent(rs));
            }
        }
        return students;
    }

    public static List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = """
                SELECT s.student_id, s.user_id, u.full_name, u.username,
                       s.roll_number, s.course, s.contact_number,
                       s.emergency_contact, s.address
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                WHERE u.full_name LIKE ?
                   OR s.roll_number LIKE ?
                   OR s.course LIKE ?
                   OR u.username LIKE ?
                ORDER BY s.student_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String term = "%" + keyword.trim() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            ps.setString(4, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        }
        return students;
    }

    public static Student getStudentById(int studentId) throws SQLException {
        String sql = """
                SELECT s.student_id, s.user_id, u.full_name, u.username,
                       s.roll_number, s.course, s.contact_number,
                       s.emergency_contact, s.address
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                WHERE s.student_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        }
        return null;
    }

    public static Student getStudentByUserId(int userId) throws SQLException {
        String sql = """
                SELECT s.student_id, s.user_id, u.full_name, u.username,
                       s.roll_number, s.course, s.contact_number,
                       s.emergency_contact, s.address
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                WHERE s.user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        }
        return null;
    }

    public static boolean addStudentWithAccount(
            String fullName,
            String username,
            String password,
            String rollNumber,
            String course,
            String contact,
            String emergency,
            String address
    ) throws SQLException {

        String userSql = """
                INSERT INTO users (full_name, username, password_hash, role)
                VALUES (?, ?, ?, 'STUDENT')
                """;

        String studentSql = """
                INSERT INTO students (user_id, roll_number, course, contact_number, emergency_contact, address)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement psUser = con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    psUser.setString(1, fullName.trim());
                    psUser.setString(2, username.trim());
                    psUser.setString(3, PasswordUtil.hashPassword(password));
                    psUser.executeUpdate();

                    try (ResultSet rs = psUser.getGeneratedKeys()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        userId = rs.getInt(1);
                    }
                }

                try (PreparedStatement psStudent = con.prepareStatement(studentSql)) {
                    psStudent.setInt(1, userId);
                    psStudent.setString(2, rollNumber.trim().toUpperCase());
                    psStudent.setString(3, course.trim());
                    psStudent.setString(4, contact.trim());
                    psStudent.setString(5, emergency.trim());
                    psStudent.setString(6, address.trim());
                    psStudent.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public static boolean updateStudent(
            int studentId,
            String fullName,
            String rollNumber,
            String course,
            String contact,
            String emergency,
            String address
    ) throws SQLException {

        String updateStudentSql = """
                UPDATE students
                SET roll_number = ?, course = ?, contact_number = ?, emergency_contact = ?, address = ?
                WHERE student_id = ?
                """;

        String updateUserSql = """
                UPDATE users u
                JOIN students s ON u.user_id = s.user_id
                SET u.full_name = ?
                WHERE s.student_id = ?
                """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(updateUserSql)) {
                    ps.setString(1, fullName.trim());
                    ps.setInt(2, studentId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(updateStudentSql)) {
                    ps.setString(1, rollNumber.trim().toUpperCase());
                    ps.setString(2, course.trim());
                    ps.setString(3, contact.trim());
                    ps.setString(4, emergency.trim());
                    ps.setString(5, address.trim());
                    ps.setInt(6, studentId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public static boolean updateStudent(
            int studentId,
            String fullName,
            String course,
            String contact,
            String emergency,
            String address
    ) throws SQLException {
        String getRollSql = "SELECT roll_number FROM students WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(getRollSql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String roll = rs.getString("roll_number");
                    return updateStudent(studentId, fullName, roll, course, contact, emergency, address);
                }
            }
        }
        return false;
    }

    public static boolean deleteStudent(int studentId) throws SQLException {
        // Deleting from users cascades to students, allocations, fees, complaints
        String sql = "DELETE FROM users WHERE user_id = (SELECT user_id FROM students WHERE student_id = ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private static Student extractStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("roll_number"),
                rs.getString("course"),
                rs.getString("contact_number"),
                rs.getString("emergency_contact"),
                rs.getString("address")
        );
    }
}
