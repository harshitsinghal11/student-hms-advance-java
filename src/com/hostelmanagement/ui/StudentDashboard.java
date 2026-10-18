package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentDashboard extends JFrame {

    public StudentDashboard() {

        setTitle("Student Dashboard");

        setSize(750, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        String userDisplay = (Session.getCurrentUser() != null)
                ? Session.getCurrentUser().getFullName()
                : "Student";

        JLabel titleLabel =
                new JLabel("Student Dashboard | Welcome, " + userDisplay);

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

        changePasswordButton.addActionListener(e ->
                new ChangePasswordDialog(this).setVisible(true));

        logoutButton.addActionListener(e ->
                logout());
    }

    private void openProfile() {
        new StudentProfileFrame().setVisible(true);
    }

    private void openRoomDetails() {
        new StudentRoomFrame().setVisible(true);
    }

    private void openFees() {
        new StudentFeesFrame().setVisible(true);
    }

    private void openComplaints() {
        new StudentComplaintsFrame().setVisible(true);
    }

    private void logout() {

        com.hostelmanagement.session.Session.clearSession();

        dispose();

        new LoginFrame().setVisible(true);
    }
}