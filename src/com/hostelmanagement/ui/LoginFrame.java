package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.*;

import com.hostelmanagement.auth.AuthService;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Student Hostel Management System - Login");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JLabel title = new JLabel(
                "Student Hostel Management System",
                SwingConstants.CENTER
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10)
        );

        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        form.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register as Student");

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(loginButton, gbc);

        gbc.gridy = 3;
        form.add(registerButton, gbc);

        add(form, BorderLayout.CENTER);

        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void performLogin() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Enter both username and password."
            );
            return;
        }

        try {
            User user = AuthService.login(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this, "Invalid credentials or inactive account."
                );
                return;
            }

            Session.setCurrentUser(user);
            dispose();

            if ("ADMIN".equals(user.getRole())) {
                new AdminDashboard().setVisible(true);
            } else if ("STUDENT".equals(user.getRole())) {
                new StudentDashboard().setVisible(true);
            } else {
                Session.clearSession();
                JOptionPane.showMessageDialog(
                        null, "Unrecognized user role."
                );
                new LoginFrame().setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to the database.\n"
                            + "Check MySQL Server and DBConnection settings.\n\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}