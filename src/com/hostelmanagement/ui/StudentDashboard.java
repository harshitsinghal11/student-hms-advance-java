package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.*;

import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentDashboard extends JFrame {

    public StudentDashboard() {

        setTitle("Student Dashboard");

        setSize(700, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel =
                new JLabel("Student Dashboard");

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton =
                UIUtils.createButton("Logout");

        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel modulePanel =
                new JPanel(new GridLayout(2, 2, 15, 15));

        modulePanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JButton profileButton =
                UIUtils.createButton("Profile");

        JButton roomDetailsButton =
                UIUtils.createButton("Room Details");

        JButton feesButton =
                UIUtils.createButton("Fees");

        JButton complaintsButton =
                UIUtils.createButton("My Complaints");

        modulePanel.add(profileButton);
        modulePanel.add(roomDetailsButton);
        modulePanel.add(feesButton);
        modulePanel.add(complaintsButton);

        add(modulePanel, BorderLayout.CENTER);

        profileButton.addActionListener(e ->
                openProfile());

        roomDetailsButton.addActionListener(e ->
                openRoomDetails());

        feesButton.addActionListener(e ->
                openFees());

        complaintsButton.addActionListener(e ->
                openComplaints());

        logoutButton.addActionListener(e ->
                logout());
    }

    private void openProfile() {

        new ModuleFrame(
                "Profile",
                new String[]{
                        "View Profile",
                        "Update Profile"
                }
        ).setVisible(true);
    }

    private void openRoomDetails() {

        new ModuleFrame(
                "Room Details",
                new String[]{
                        "View Allocated Room"
                }
        ).setVisible(true);
    }

    private void openFees() {

        new ModuleFrame(
                "Fees",
                new String[]{
                        "View Fee Details",
                        "View Payment Status"
                }
        ).setVisible(true);
    }

    private void openComplaints() {

        new ModuleFrame(
                "My Complaints",
                new String[]{
                        "Submit Complaint",
                        "View My Complaints"
                }
        ).setVisible(true);
    }

    private void logout() {

        com.hostelmanagement.session.Session.clearSession();

        dispose();

        new LoginFrame().setVisible(true);
    }
}