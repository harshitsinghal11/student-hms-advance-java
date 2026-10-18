package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.Allocation;

public class AllocationDAO {

    public static List<Allocation> getAllAllocations() throws SQLException {
        List<Allocation> list = new ArrayList<>();
        String sql = """
                SELECT a.allocation_id, a.student_id, u.full_name, s.roll_number,
                       a.room_id, r.room_number, r.block_wing,
                       a.allocation_date, a.vacate_date, a.status
                FROM allocations a
                JOIN students s ON a.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                JOIN rooms r ON a.room_id = r.room_id
                ORDER BY a.allocation_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractAllocation(rs));
            }
        }
        return list;
    }

    public static Allocation getActiveAllocationByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT a.allocation_id, a.student_id, u.full_name, s.roll_number,
                       a.room_id, r.room_number, r.block_wing,
                       a.allocation_date, a.vacate_date, a.status
                FROM allocations a
                JOIN students s ON a.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                JOIN rooms r ON a.room_id = r.room_id
                WHERE a.student_id = ?
                  AND a.status = 'ACTIVE'
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAllocation(rs);
                }
            }
        }
        return null;
    }

    public static boolean allocateRoom(int studentId, int roomId) throws SQLException {
        String checkStudent = "SELECT COUNT(*) FROM allocations WHERE student_id = ? AND status = 'ACTIVE'";
        String checkRoom = "SELECT capacity, current_occupancy, room_status FROM rooms WHERE room_id = ?";
        String insertAlloc = "INSERT INTO allocations (student_id, room_id, allocation_date, status) VALUES (?, ?, CURRENT_DATE, 'ACTIVE')";
        String updateRoom = "UPDATE rooms SET current_occupancy = current_occupancy + 1, room_status = CASE WHEN current_occupancy + 1 >= capacity THEN 'FULL' ELSE 'AVAILABLE' END WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(checkStudent)) {
                    ps.setInt(1, studentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            con.rollback();
                            return false; // Student already has an active room allocation
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(checkRoom)) {
                    ps.setInt(1, roomId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        int capacity = rs.getInt("capacity");
                        int occupancy = rs.getInt("current_occupancy");
                        String status = rs.getString("room_status");

                        if (!"AVAILABLE".equalsIgnoreCase(status) || occupancy >= capacity) {
                            con.rollback();
                            return false; // Room is full or in maintenance
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(insertAlloc)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, roomId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(updateRoom)) {
                    ps.setInt(1, roomId);
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

    public static boolean changeRoom(int studentId, int newRoomId) throws SQLException {
        String findOld = "SELECT allocation_id, room_id FROM allocations WHERE student_id = ? AND status = 'ACTIVE'";
        String checkNewRoom = "SELECT capacity, current_occupancy, room_status FROM rooms WHERE room_id = ?";
        String updateOldAlloc = "UPDATE allocations SET status = 'TRANSFERRED', vacate_date = CURRENT_DATE WHERE allocation_id = ?";
        String decOldRoom = "UPDATE rooms SET current_occupancy = GREATEST(0, current_occupancy - 1), room_status = 'AVAILABLE' WHERE room_id = ?";
        String insertNewAlloc = "INSERT INTO allocations (student_id, room_id, allocation_date, status) VALUES (?, ?, CURRENT_DATE, 'ACTIVE')";
        String incNewRoom = "UPDATE rooms SET current_occupancy = current_occupancy + 1, room_status = CASE WHEN current_occupancy + 1 >= capacity THEN 'FULL' ELSE 'AVAILABLE' END WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int oldAllocId;
                int oldRoomId;

                try (PreparedStatement ps = con.prepareStatement(findOld)) {
                    ps.setInt(1, studentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false; // No active room to transfer from
                        }
                        oldAllocId = rs.getInt("allocation_id");
                        oldRoomId = rs.getInt("room_id");
                    }
                }

                if (oldRoomId == newRoomId) {
                    con.rollback();
                    return false; // Already in this room
                }

                try (PreparedStatement ps = con.prepareStatement(checkNewRoom)) {
                    ps.setInt(1, newRoomId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt("current_occupancy") >= rs.getInt("capacity") || !"AVAILABLE".equalsIgnoreCase(rs.getString("room_status"))) {
                            con.rollback();
                            return false; // New room not available
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(updateOldAlloc)) {
                    ps.setInt(1, oldAllocId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(decOldRoom)) {
                    ps.setInt(1, oldRoomId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(insertNewAlloc)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, newRoomId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(incNewRoom)) {
                    ps.setInt(1, newRoomId);
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

    public static boolean vacateRoom(int allocationId) throws SQLException {
        String findAlloc = "SELECT room_id FROM allocations WHERE allocation_id = ? AND status = 'ACTIVE'";
        String updateAlloc = "UPDATE allocations SET status = 'VACATED', vacate_date = CURRENT_DATE WHERE allocation_id = ?";
        String decRoom = "UPDATE rooms SET current_occupancy = GREATEST(0, current_occupancy - 1), room_status = 'AVAILABLE' WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int roomId;
                try (PreparedStatement ps = con.prepareStatement(findAlloc)) {
                    ps.setInt(1, allocationId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        roomId = rs.getInt("room_id");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(updateAlloc)) {
                    ps.setInt(1, allocationId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(decRoom)) {
                    ps.setInt(1, roomId);
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

    private static Allocation extractAllocation(ResultSet rs) throws SQLException {
        return new Allocation(
                rs.getInt("allocation_id"),
                rs.getInt("student_id"),
                rs.getString("full_name"),
                rs.getString("roll_number"),
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getString("block_wing"),
                rs.getDate("allocation_date"),
                rs.getDate("vacate_date"),
                rs.getString("status")
        );
    }
}
