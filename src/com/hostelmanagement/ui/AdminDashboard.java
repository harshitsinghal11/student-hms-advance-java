package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin / Warden Dashboard");

        setSize(850, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        String userDisplay = (Session.getCurrentUser() != null)
                ? Session.getCurrentUser().getFullName()
                : "Admin";

        JLabel titleLabel =
                new JLabel("Admin / Warden Dashboard | Logged in as: " + userDisplay);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton changePasswordButton =
                UIUtils.createButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(150, 32));

        JButton logoutButton =
                UIUtils.createButton("Logout");
        logoutButton.setPreferredSize(new Dimension(90, 32));

        actionPanel.add(changePasswordButton);
        actionPanel.add(logoutButton);

        topPanel.add(actionPanel, BorderLayout.EAST);

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

        changePasswordButton.addActionListener(e ->
                new ChangePasswordDialog(this).setVisible(true));

        logoutButton.addActionListener(e ->
                logout());
    }

    private void openStudentManagement() {
        new StudentManagementFrame().setVisible(true);
    }

    private void openRoomManagement() {
        new RoomManagementFrame().setVisible(true);
    }

    private void openRoomAllocation() {
        new RoomAllocationFrame().setVisible(true);
    }

    private void openFeeManagement() {
        new FeeManagementFrame().setVisible(true);
    }

    private void openComplaintManagement() {
        new ComplaintManagementFrame().setVisible(true);
    }

    private void openReports() {
        new ReportsFrame().setVisible(true);
    }

    private void logout() {

        com.hostelmanagement.session.Session.clearSession();

        dispose();

        new LoginFrame().setVisible(true);
    }
}