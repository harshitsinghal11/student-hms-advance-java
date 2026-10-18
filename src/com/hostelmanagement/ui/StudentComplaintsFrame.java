package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.ComplaintDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Complaint;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentComplaintsFrame extends JFrame {

    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private Student currentStudent;

    public StudentComplaintsFrame() {
        setTitle("My Complaints & Maintenance Requests");
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadComplaintData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Grievance Redressal — My Complaints");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton addBtn = UIUtils.createButton("+ Submit Complaint", 145, 30);
        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);
        actionPanel.add(addBtn);
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Category", "Description", "Status", "Filed Date", "Warden Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        complaintTable = new JTable(tableModel);
        UIUtils.styleTable(complaintTable);
        complaintTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        complaintTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        add(new JScrollPane(complaintTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton closeBtn = UIUtils.createButton("Close", 90, 32);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());
        refreshBtn.addActionListener(e -> loadComplaintData());
        addBtn.addActionListener(e -> showSubmitComplaintDialog());
    }

    private void loadComplaintData() {
        User user = Session.getCurrentUser();
        if (user == null) {
            UIUtils.showError(this, "No active session.");
            dispose();
            return;
        }

        try {
            currentStudent = StudentDAO.getStudentByUserId(user.getUserId());
            tableModel.setRowCount(0);

            if (currentStudent != null) {
                List<Complaint> complaints = ComplaintDAO.getComplaintsByStudentId(currentStudent.getStudentId());
                for (Complaint c : complaints) {
                    tableModel.addRow(new Object[]{
                            c.getComplaintId(),
                            c.getCategory(),
                            c.getDescription(),
                            c.getStatus(),
                            c.getFiledAt(),
                            c.getResolutionRemarks() != null ? c.getResolutionRemarks() : "-"
                    });
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load complaints:\n" + e.getMessage());
        }
    }

    private void showSubmitComplaintDialog() {
        if (currentStudent == null) {
            UIUtils.showMessage(this, "Your student profile must be registered before filing complaints.");
            return;
        }

        JDialog dialog = new JDialog(this, "Submit New Maintenance / Hostel Complaint", true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel categoryRow = new JPanel(new GridLayout(1, 2, 8, 8));
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"ELECTRICAL", "PLUMBING", "CLEANLINESS", "FOOD", "OTHER"});
        categoryRow.add(new JLabel("Complaint Category:"));
        categoryRow.add(categoryBox);
        centerPanel.add(categoryRow, BorderLayout.NORTH);

        JPanel descRow = new JPanel(new BorderLayout(5, 5));
        descRow.add(new JLabel("Issue Description:"), BorderLayout.NORTH);
        JTextArea descArea = new JTextArea(5, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descRow.add(new JScrollPane(descArea), BorderLayout.CENTER);
        centerPanel.add(descRow, BorderLayout.CENTER);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton submitBtn = UIUtils.createButton("Submit Grievance", 140, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        submitBtn.addActionListener(e -> {
            String cat = (String) categoryBox.getSelectedItem();
            String desc = descArea.getText().trim();

            if (desc.isEmpty()) {
                UIUtils.showError(dialog, "Please enter a description of the issue.");
                return;
            }

            try {
                boolean ok = ComplaintDAO.submitComplaint(currentStudent.getStudentId(), cat, desc);
                if (ok) {
                    UIUtils.showMessage(this, "Complaint registered successfully! The warden has been notified.");
                    dialog.dispose();
                    loadComplaintData();
                } else {
                    UIUtils.showError(dialog, "Could not submit complaint.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}
