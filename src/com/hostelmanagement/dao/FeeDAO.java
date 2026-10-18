package com.hostelmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hostelmanagement.database.DBConnection;
import com.hostelmanagement.model.Fee;

public class FeeDAO {

    public static List<Fee> getAllFees() throws SQLException {
        List<Fee> fees = new ArrayList<>();
        String sql = """
                SELECT f.fee_id, f.student_id, u.full_name, s.roll_number,
                       f.amount_due, f.amount_paid, f.due_date,
                       f.payment_date, f.payment_status, f.transaction_ref
                FROM fees f
                JOIN students s ON f.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                ORDER BY f.fee_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                fees.add(extractFee(rs));
            }
        }
        return fees;
    }

    public static List<Fee> getPendingFees() throws SQLException {
        List<Fee> fees = new ArrayList<>();
        String sql = """
                SELECT f.fee_id, f.student_id, u.full_name, s.roll_number,
                       f.amount_due, f.amount_paid, f.due_date,
                       f.payment_date, f.payment_status, f.transaction_ref
                FROM fees f
                JOIN students s ON f.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                WHERE f.payment_status IN ('PENDING', 'OVERDUE')
                ORDER BY f.due_date ASC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                fees.add(extractFee(rs));
            }
        }
        return fees;
    }

    public static List<Fee> getFeesByStudentId(int studentId) throws SQLException {
        List<Fee> fees = new ArrayList<>();
        String sql = """
                SELECT f.fee_id, f.student_id, u.full_name, s.roll_number,
                       f.amount_due, f.amount_paid, f.due_date,
                       f.payment_date, f.payment_status, f.transaction_ref
                FROM fees f
                JOIN students s ON f.student_id = s.student_id
                JOIN users u ON s.user_id = u.user_id
                WHERE f.student_id = ?
                ORDER BY f.fee_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fees.add(extractFee(rs));
                }
            }
        }
        return fees;
    }

    public static boolean recordFee(int studentId, double amountDue, Date dueDate) throws SQLException {
        String sql = """
                INSERT INTO fees (student_id, amount_due, amount_paid, due_date, payment_status)
                VALUES (?, ?, 0.00, ?, 'PENDING')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDouble(2, amountDue);
            ps.setDate(3, dueDate);

            return ps.executeUpdate() == 1;
        }
    }

    public static boolean recordPayment(int feeId, double amountPaid, String transactionRef) throws SQLException {
        String querySql = "SELECT amount_due, amount_paid FROM fees WHERE fee_id = ?";
        String updateSql = """
                UPDATE fees
                SET amount_paid = ?, payment_date = CURRENT_DATE,
                    payment_status = ?, transaction_ref = ?
                WHERE fee_id = ?
                """;

        try (Connection con = DBConnection.getConnection()) {
            double amountDue;
            double currentPaid;

            try (PreparedStatement ps = con.prepareStatement(querySql)) {
                ps.setInt(1, feeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    amountDue = rs.getDouble("amount_due");
                    currentPaid = rs.getDouble("amount_paid");
                }
            }

            double totalPaid = currentPaid + amountPaid;
            String newStatus = totalPaid >= amountDue ? "PAID" : "PENDING";

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setDouble(1, totalPaid);
                ps.setString(2, newStatus);
                ps.setString(3, transactionRef != null ? transactionRef.trim() : "CASH-" + System.currentTimeMillis());
                ps.setInt(4, feeId);

                return ps.executeUpdate() == 1;
            }
        }
    }

    public static boolean updatePaymentStatus(int feeId, String status) throws SQLException {
        String sql = "UPDATE fees SET payment_status = ? WHERE fee_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, feeId);

            return ps.executeUpdate() == 1;
        }
    }

    private static Fee extractFee(ResultSet rs) throws SQLException {
        return new Fee(
                rs.getInt("fee_id"),
                rs.getInt("student_id"),
                rs.getString("full_name"),
                rs.getString("roll_number"),
                rs.getDouble("amount_due"),
                rs.getDouble("amount_paid"),
                rs.getDate("due_date"),
                rs.getDate("payment_date"),
                rs.getString("payment_status"),
                rs.getString("transaction_ref")
        );
    }
}
