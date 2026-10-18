package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.Room;

public class RoomDAO {

    public static List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
                SELECT room_id, room_number, block_wing, capacity,
                       current_occupancy, monthly_fee, room_status
                FROM rooms
                ORDER BY room_number ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(extractRoom(rs));
            }
        }
        return rooms;
    }

    public static List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
                SELECT room_id, room_number, block_wing, capacity,
                       current_occupancy, monthly_fee, room_status
                FROM rooms
                WHERE room_status = 'AVAILABLE'
                  AND current_occupancy < capacity
                ORDER BY room_number ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(extractRoom(rs));
            }
        }
        return rooms;
    }

    public static List<Room> getOccupiedRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
                SELECT room_id, room_number, block_wing, capacity,
                       current_occupancy, monthly_fee, room_status
                FROM rooms
                WHERE current_occupancy > 0
                ORDER BY room_number ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(extractRoom(rs));
            }
        }
        return rooms;
    }

    public static Room getRoomById(int roomId) throws SQLException {
        String sql = """
                SELECT room_id, room_number, block_wing, capacity,
                       current_occupancy, monthly_fee, room_status
                FROM rooms
                WHERE room_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractRoom(rs);
                }
            }
        }
        return null;
    }

    public static boolean addRoom(
            String roomNumber,
            String blockWing,
            int capacity,
            double monthlyFee
    ) throws SQLException {

        String sql = """
                INSERT INTO rooms (room_number, block_wing, capacity, current_occupancy, monthly_fee, room_status)
                VALUES (?, ?, ?, 0, ?, 'AVAILABLE')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomNumber.trim().toUpperCase());
            ps.setString(2, blockWing.trim());
            ps.setInt(3, capacity);
            ps.setDouble(4, monthlyFee);

            return ps.executeUpdate() == 1;
        }
    }

    public static boolean updateRoom(
            int roomId,
            String roomNumber,
            String blockWing,
            int capacity,
            double monthlyFee,
            String roomStatus
    ) throws SQLException {

        String sql = """
                UPDATE rooms
                SET room_number = ?, block_wing = ?, capacity = ?,
                    monthly_fee = ?, room_status = ?
                WHERE room_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomNumber.trim().toUpperCase());
            ps.setString(2, blockWing.trim());
            ps.setInt(3, capacity);
            ps.setDouble(4, monthlyFee);
            ps.setString(5, roomStatus);
            ps.setInt(6, roomId);

            return ps.executeUpdate() == 1;
        }
    }

    public static boolean deleteRoom(int roomId) throws SQLException {
        // Prevent deletion if room currently has active allocations
        String checkSql = "SELECT COUNT(*) FROM allocations WHERE room_id = ? AND status = 'ACTIVE'";
        String deleteSql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setInt(1, roomId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false; // active allocations exist
                    }
                }
            }

            try (PreparedStatement delPs = con.prepareStatement(deleteSql)) {
                delPs.setInt(1, roomId);
                return delPs.executeUpdate() == 1;
            }
        }
    }

    private static Room extractRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getString("block_wing"),
                rs.getInt("capacity"),
                rs.getInt("current_occupancy"),
                rs.getDouble("monthly_fee"),
                rs.getString("room_status")
        );
    }
}
