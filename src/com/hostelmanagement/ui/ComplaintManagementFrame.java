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
import com.hostelmanagement.model.Complaint;
import com.hostelmanagement.util.UIUtils;

public class ComplaintManagementFrame extends JFrame {

    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private List<Complaint> currentComplaints;

    public ComplaintManagementFrame() {
        setTitle("Complaint & Maintenance Management");
        setSize(950, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadComplaintData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Complaint Management — Grievance Tracking & Resolution");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Student Name", "Roll Number", "Room", "Category", "Description", "Status", "Filed Date", "Resolution Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintTable = new JTable(tableModel);
        UIUtils.styleTable(complaintTable);
        complaintTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        complaintTable.getColumnModel().getColumn(8).setPreferredWidth(160);
        add(new JScrollPane(complaintTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton resolveBtn = UIUtils.createButton("Update Status / Resolve", 170, 32);
        bottomPanel.add(resolveBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        refreshBtn.addActionListener(e -> loadComplaintData());
        resolveBtn.addActionListener(e -> showUpdateStatusDialog());
    }

    private void loadComplaintData() {
        try {
            currentComplaints = ComplaintDAO.getAllComplaints();
            tableModel.setRowCount(0);

            for (Complaint c : currentComplaints) {
                tableModel.addRow(new Object[]{
                        c.getComplaintId(),
                        c.getStudentName(),
                        c.getRollNumber(),
                        c.getRoomNumber(),
                        c.getCategory(),
                        c.getDescription(),
                        c.getStatus(),
                        c.getFiledAt(),
                        c.getResolutionRemarks() != null ? c.getResolutionRemarks() : "-"
                });
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load complaints:\n" + e.getMessage());
        }
    }

    private void showUpdateStatusDialog() {
        int selectedRow = complaintTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a complaint from the table to update.");
            return;
        }

        Complaint complaint = currentComplaints.get(selectedRow);

        JDialog dialog = new JDialog(this, "Update Complaint #" + complaint.getComplaintId(), true);
        dialog.setSize(440, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"PENDING", "IN_PROGRESS", "RESOLVED"});
        statusBox.setSelectedItem(complaint.getStatus());

        form.add(new JLabel("Complaint Status:"));
        form.add(statusBox);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        centerPanel.add(form, BorderLayout.NORTH);

        JPanel remarkPanel = new JPanel(new BorderLayout(5, 5));
        remarkPanel.add(new JLabel("Resolution Remarks:"), BorderLayout.NORTH);
        JTextArea remarksArea = new JTextArea(complaint.getResolutionRemarks() != null ? complaint.getResolutionRemarks() : "", 4, 25);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        remarkPanel.add(new JScrollPane(remarksArea), BorderLayout.CENTER);
        centerPanel.add(remarkPanel, BorderLayout.CENTER);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Save Update", 120, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String newStatus = (String) statusBox.getSelectedItem();
            String remarks = remarksArea.getText().trim();

            try {
                boolean ok = ComplaintDAO.updateComplaintStatus(complaint.getComplaintId(), newStatus, remarks);
                if (ok) {
                    UIUtils.showMessage(this, "Complaint status updated successfully!");
                    dialog.dispose();
                    loadComplaintData();
                } else {
                    UIUtils.showError(dialog, "Failed to update complaint.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}
