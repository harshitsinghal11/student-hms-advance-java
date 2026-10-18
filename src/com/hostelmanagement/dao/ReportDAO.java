package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.database.DBConnection;

public class ReportDAO {

    public static List<String[]> getStudentReport() throws SQLException {
        List<String[]> data = new ArrayList<>();
        String sql = """
                SELECT s.roll_number, u.full_name, s.course, s.contact_number,
                       COALESCE(r.room_number, 'Unallocated') AS room_no,
                       COALESCE(r.block_wing, '-') AS block
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                LEFT JOIN allocations a ON (s.student_id = a.student_id AND a.status = 'ACTIVE')
                LEFT JOIN rooms r ON a.room_id = r.room_id
                ORDER BY s.roll_number ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("roll_number"),
                        rs.getString("full_name"),
                        rs.getString("course"),
                        rs.getString("contact_number"),
                        rs.getString("room_no"),
                        rs.getString("block")
                });
            }
        }
        return data;
    }

    public static List<String[]> getRoomReport() throws SQLException {
        List<String[]> data = new ArrayList<>();
        String sql = """
                SELECT room_number, block_wing, capacity, current_occupancy,
                       (capacity - current_occupancy) AS available_beds,
                       monthly_fee, room_status
                FROM rooms
                ORDER BY room_number ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("room_number"),
                        rs.getString("block_wing"),
                        String.valueOf(rs.getInt("capacity")),
                        String.valueOf(rs.getInt("current_occupancy")),
                        String.valueOf(rs.getInt("available_beds")),
                        String.format("%.2f", rs.getDouble("monthly_fee")),
                        rs.getString("room_status")
                });
            }
        }
        return data;
    }

    public static List<String[]> getFeeReport() throws SQLException {
        List<String[]> data = new ArrayList<>();
        String sql = """
                SELECT s.roll_number, u.full_name, f.amount_due, f.amount_paid,
                       (f.amount_due - f.amount_paid) AS pending_due,
                       f.due_date, f.payment_status,
                       COALESCE(f.transaction_ref, '-') AS tx_ref
                FROM fees f
                JOIN students s ON f.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                ORDER BY f.due_date DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("roll_number"),
                        rs.getString("full_name"),
                        String.format("%.2f", rs.getDouble("amount_due")),
                        String.format("%.2f", rs.getDouble("amount_paid")),
                        String.format("%.2f", rs.getDouble("pending_due")),
                        rs.getDate("due_date").toString(),
                        rs.getString("payment_status"),
                        rs.getString("tx_ref")
                });
            }
        }
        return data;
    }

    public static String getOccupancySummary() throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total_rooms,
                       COALESCE(SUM(capacity), 0) AS total_capacity,
                       COALESCE(SUM(current_occupancy), 0) AS total_occupied
                FROM rooms
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int totalRooms = rs.getInt("total_rooms");
                int totalCapacity = rs.getInt("total_capacity");
                int totalOccupied = rs.getInt("total_occupied");
                int vacantBeds = Math.max(0, totalCapacity - totalOccupied);
                double occupancyRate = totalCapacity > 0 ? (totalOccupied * 100.0 / totalCapacity) : 0.0;

                return String.format(
                        "Total Rooms: %d | Bed Capacity: %d | Occupied: %d | Vacant: %d | Occupancy Rate: %.1f%%",
                        totalRooms, totalCapacity, totalOccupied, vacantBeds, occupancyRate
                );
            }
        }
        return "No room data available.";
    }

    public static List<String[]> getComplaintReport() throws SQLException {
        List<String[]> data = new ArrayList<>();
        String sql = """
                SELECT c.complaint_id, s.roll_number, u.full_name, c.category,
                       c.status, c.filed_at,
                       COALESCE(c.resolution_remarks, '-') AS remarks
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                ORDER BY c.complaint_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new String[]{
                        String.valueOf(rs.getInt("complaint_id")),
                        rs.getString("roll_number"),
                        rs.getString("full_name"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getTimestamp("filed_at").toString(),
                        rs.getString("remarks")
                });
            }
        }
        return data;
    }
}
