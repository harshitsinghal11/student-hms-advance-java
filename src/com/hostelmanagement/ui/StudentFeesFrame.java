package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.FeeDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Fee;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentFeesFrame extends JFrame {

    private JTable feeTable;
    private DefaultTableModel tableModel;

    public StudentFeesFrame() {
        setTitle("My Hostel Fees & Payment Invoices");
        setSize(820, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadFeeData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Hostel Dues & Payment Status");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Invoice ID", "Amount Due (₹)", "Amount Paid (₹)", "Pending Due (₹)", "Due Date", "Payment Date", "Status", "Receipt Reference"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        feeTable = new JTable(tableModel);
        UIUtils.styleTable(feeTable);
        add(new JScrollPane(feeTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton closeBtn = UIUtils.createButton("Close", 90, 32);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());
        refreshBtn.addActionListener(e -> loadFeeData());
    }

    private void loadFeeData() {
        User user = Session.getCurrentUser();
        if (user == null) {
            UIUtils.showError(this, "No active session.");
            dispose();
            return;
        }

        try {
            Student student = StudentDAO.getStudentByUserId(user.getUserId());
            tableModel.setRowCount(0);

            if (student != null) {
                List<Fee> fees = FeeDAO.getFeesByStudentId(student.getStudentId());
                for (Fee f : fees) {
                    tableModel.addRow(new Object[]{
                            f.getFeeId(),
                            String.format("%.2f", f.getAmountDue()),
                            String.format("%.2f", f.getAmountPaid()),
                            String.format("%.2f", f.getPendingAmount()),
                            f.getDueDate(),
                            f.getPaymentDate() != null ? f.getPaymentDate() : "-",
                            f.getPaymentStatus(),
                            f.getTransactionRef() != null ? f.getTransactionRef() : "-"
                    });
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load fee records:\n" + e.getMessage());
        }
    }
}
