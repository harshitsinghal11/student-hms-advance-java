package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import com.hostelmanagement.auth.AuthService;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("Student Hostel Management System - Login");

        setSize(450, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JLabel titleLabel =
                new JLabel("Student Hostel Management System");

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );

        add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");

        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");

        passwordField = new JPasswordField(20);

        JButton loginButton =
                UIUtils.createButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;

        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;

        panel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;

        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> performLogin());

        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            UIUtils.showMessage(
                    this,
                    "Please enter username and password."
            );

            return;
        }

        User user =
                AuthService.login(username, password);

        if (user == null) {

            UIUtils.showMessage(
                    this,
                    "Invalid username or password."
            );

            return;
        }

        Session.setCurrentUser(user);

        dispose();

        if (user.getRole().equals("ADMIN")) {

            new AdminDashboard().setVisible(true);

        } else if (user.getRole().equals("STUDENT")) {

            new StudentDashboard().setVisible(true);
        }
    }
}