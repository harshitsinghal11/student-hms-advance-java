package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.Complaint;

public class ComplaintDAO {

    public static List<Complaint> getAllComplaints() throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = """
                SELECT c.complaint_id, c.student_id, u.full_name, s.roll_number,
                       COALESCE(r.room_number, 'N/A') AS room_number,
                       c.category, c.description, c.status,
                       c.resolution_remarks, c.filed_at, c.resolved_at
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                LEFT JOIN allocations a ON (s.student_id = a.student_id AND a.status = 'ACTIVE')
                LEFT JOIN rooms r ON a.room_id = r.room_id
                ORDER BY c.complaint_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractComplaint(rs));
            }
        }
        return list;
    }

    public static List<Complaint> getComplaintsByStudentId(int studentId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = """
                SELECT c.complaint_id, c.student_id, u.full_name, s.roll_number,
                       COALESCE(r.room_number, 'N/A') AS room_number,
                       c.category, c.description, c.status,
                       c.resolution_remarks, c.filed_at, c.resolved_at
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                LEFT JOIN allocations a ON (s.student_id = a.student_id AND a.status = 'ACTIVE')
                LEFT JOIN rooms r ON a.room_id = r.room_id
                WHERE c.student_id = ?
                ORDER BY c.complaint_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractComplaint(rs));
                }
            }
        }
        return list;
    }

    public static boolean submitComplaint(int studentId, String category, String description) throws SQLException {
        String sql = """
                INSERT INTO complaints (student_id, category, description, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, category);
            ps.setString(3, description.trim());

            return ps.executeUpdate() == 1;
        }
    }

    public static boolean updateComplaintStatus(int complaintId, String status, String remarks) throws SQLException {
        String sql = """
                UPDATE complaints
                SET status = ?, resolution_remarks = ?,
                    resolved_at = CASE WHEN ? = 'RESOLVED' THEN CURRENT_TIMESTAMP ELSE NULL END
                WHERE complaint_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, remarks != null ? remarks.trim() : null);
            ps.setString(3, status);
            ps.setInt(4, complaintId);

            return ps.executeUpdate() == 1;
        }
    }

    private static Complaint extractComplaint(ResultSet rs) throws SQLException {
        return new Complaint(
                rs.getInt("complaint_id"),
                rs.getInt("student_id"),
                rs.getString("full_name"),
                rs.getString("roll_number"),
                rs.getString("room_number"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getString("resolution_remarks"),
                rs.getTimestamp("filed_at"),
                rs.getTimestamp("resolved_at")
        );
    }
}
