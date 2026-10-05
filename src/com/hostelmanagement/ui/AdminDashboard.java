package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.*;

import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin / Warden Dashboard");

        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel =
                new JLabel("Admin / Warden Dashboard");

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton =
                UIUtils.createButton("Logout");

        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel modulePanel =
                new JPanel(new GridLayout(3, 2, 15, 15));

        modulePanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JButton studentManagement =
                UIUtils.createButton("Student Management");

        JButton roomManagement =
                UIUtils.createButton("Room Management");

        JButton roomAllocation =
                UIUtils.createButton("Room Allocation");

        JButton feeManagement =
                UIUtils.createButton("Fee Management");

        JButton complaintManagement =
                UIUtils.createButton("Complaint Management");

        JButton reports =
                UIUtils.createButton("Reports");

        modulePanel.add(studentManagement);
        modulePanel.add(roomManagement);
        modulePanel.add(roomAllocation);
        modulePanel.add(feeManagement);
        modulePanel.add(complaintManagement);
        modulePanel.add(reports);

        add(modulePanel, BorderLayout.CENTER);

        studentManagement.addActionListener(e ->
                openStudentManagement());

        roomManagement.addActionListener(e ->
                openRoomManagement());

        roomAllocation.addActionListener(e ->
                openRoomAllocation());

        feeManagement.addActionListener(e ->
                openFeeManagement());

        complaintManagement.addActionListener(e ->
                openComplaintManagement());

        reports.addActionListener(e ->
                openReports());

        logoutButton.addActionListener(e ->
                logout());
    }

    private void openStudentManagement() {

        new ModuleFrame(
                "Student Management",
                new String[]{
                        "Add Student",
                        "View Student",
                        "Search Student",
                        "Update Student",
                        "Delete Student"
                }
        ).setVisible(true);
    }

    private void openRoomManagement() {

        new ModuleFrame(
                "Room Management",
                new String[]{
                        "View Room",
                        "Add Room",
                        "Update Room",
                        "Delete Room",
                        "View Available Rooms",
                        "View Occupied Rooms"
                }
        ).setVisible(true);
    }

    private void openRoomAllocation() {

        new ModuleFrame(
                "Room Allocation",
                new String[]{
                        "Allocate Room",
                        "Change Room",
                        "Vacate Room",
                        "View Allocation"
                }
        ).setVisible(true);
    }

    private void openFeeManagement() {

        new ModuleFrame(
                "Fee Management",
                new String[]{
                        "Record Fee",
                        "View Fee",
                        "View Pending Fees",
                        "Record Payment",
                        "Update Payment Status"
                }
        ).setVisible(true);
    }

    private void openComplaintManagement() {

        new ModuleFrame(
                "Complaint Management",
                new String[]{
                        "View Complaints",
                        "View Complaint Details",
                        "Update Status",
                        "Resolve Complaint"
                }
        ).setVisible(true);
    }

    private void openReports() {

        new ModuleFrame(
                "Reports",
                new String[]{
                        "Student Report",
                        "Room Report",
                        "Fee Report",
                        "Occupancy Report",
                        "Complaint Report"
                }
        ).setVisible(true);
    }

    private void logout() {

        Session.clearSession();

        dispose();

        new LoginFrame().setVisible(true);
    }
}