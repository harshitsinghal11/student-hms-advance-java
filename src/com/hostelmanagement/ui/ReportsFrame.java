package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.ReportDAO;
import com.hostelmanagement.util.UIUtils;

public class ReportsFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JLabel occupancyBanner;

    private DefaultTableModel studentModel;
    private DefaultTableModel roomModel;
    private DefaultTableModel feeModel;
    private DefaultTableModel complaintModel;

    public ReportsFrame() {
        setTitle("Hostel Administrative Reports");
        setSize(980, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadAllReports();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Hostel Management — Analytical & Audit Reports");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshAllBtn = UIUtils.createButton("Refresh All Reports", 150, 30);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.add(refreshAllBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // Tab 1: Student Report
        JPanel studentPanel = new JPanel(new BorderLayout());
        String[] studentCols = {"Roll Number", "Full Name", "Course", "Contact", "Allocated Room", "Block / Wing"};
        studentModel = new DefaultTableModel(studentCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable studentTable = new JTable(studentModel);
        UIUtils.styleTable(studentTable);
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        tabbedPane.addTab("Student Demographics", studentPanel);

        // Tab 2: Room & Occupancy Report
        JPanel roomPanel = new JPanel(new BorderLayout());
        occupancyBanner = new JLabel("Loading occupancy summary...", JLabel.CENTER);
        occupancyBanner.setFont(new Font("Arial", Font.BOLD, 13));
        occupancyBanner.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        roomPanel.add(occupancyBanner, BorderLayout.NORTH);

        String[] roomCols = {"Room Number", "Block / Wing", "Capacity", "Occupied Beds", "Vacant Beds", "Monthly Fee (₹)", "Status"};
        roomModel = new DefaultTableModel(roomCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable roomTable = new JTable(roomModel);
        UIUtils.styleTable(roomTable);
        roomPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        tabbedPane.addTab("Room & Occupancy", roomPanel);

        // Tab 3: Fee Collection Report
        JPanel feePanel = new JPanel(new BorderLayout());
        String[] feeCols = {"Roll Number", "Student Name", "Amount Due (₹)", "Amount Paid (₹)", "Pending Due (₹)", "Due Date", "Status", "Receipt Ref"};
        feeModel = new DefaultTableModel(feeCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable feeTable = new JTable(feeModel);
        UIUtils.styleTable(feeTable);
        feePanel.add(new JScrollPane(feeTable), BorderLayout.CENTER);
        tabbedPane.addTab("Fee Auditing", feePanel);

        // Tab 4: Complaint Report
        JPanel complaintPanel = new JPanel(new BorderLayout());
        String[] complaintCols = {"ID", "Roll Number", "Student Name", "Category", "Status", "Filed Date", "Resolution Remarks"};
        complaintModel = new DefaultTableModel(complaintCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable complaintTable = new JTable(complaintModel);
        UIUtils.styleTable(complaintTable);
        complaintPanel.add(new JScrollPane(complaintTable), BorderLayout.CENTER);
        tabbedPane.addTab("Complaint Redressal", complaintPanel);

        add(tabbedPane, BorderLayout.CENTER);

        refreshAllBtn.addActionListener(e -> loadAllReports());
    }

    private void loadAllReports() {
        try {
            // Student Report
            studentModel.setRowCount(0);
            List<String[]> students = ReportDAO.getStudentReport();
            for (String[] row : students) {
                studentModel.addRow(row);
            }

            // Room Report & Occupancy Summary
            occupancyBanner.setText(ReportDAO.getOccupancySummary());
            roomModel.setRowCount(0);
            List<String[]> rooms = ReportDAO.getRoomReport();
            for (String[] row : rooms) {
                roomModel.addRow(row);
            }

            // Fee Report
            feeModel.setRowCount(0);
            List<String[]> fees = ReportDAO.getFeeReport();
            for (String[] row : fees) {
                feeModel.addRow(row);
            }

            // Complaint Report
            complaintModel.setRowCount(0);
            List<String[]> complaints = ReportDAO.getComplaintReport();
            for (String[] row : complaints) {
                complaintModel.addRow(row);
            }

        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to compile reports:\n" + e.getMessage());
        }
    }
}
