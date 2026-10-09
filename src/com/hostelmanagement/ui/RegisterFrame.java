package com.hostelmanagement.ui;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.*;

import com.hostelmanagement.auth.AuthService;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {

        setTitle("Student Registration");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        nameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Create Account");
        JButton backButton = new JButton("Back to Login");

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        panel.add(registerButton);
        panel.add(backButton);

        add(panel);

        registerButton.addActionListener(e -> registerStudent());

        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void registerStudent() {

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();

        char[] passwordChars = passwordField.getPassword();
        char[] confirmChars = confirmPasswordField.getPassword();

        String password = new String(passwordChars);
        String confirmPassword = new String(confirmChars);

        try {
            if (name.isEmpty() || username.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this, "All fields are required."
                );
                return;
            }

            if (name.length() > 100 || username.length() > 50) {
                JOptionPane.showMessageDialog(
                        this, "Name or username is too long."
                );
                return;
            }

            if (username.length() < 3
                    || !username.matches("[A-Za-z0-9._-]+")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Username must contain at least 3 characters "
                                + "and use only letters, numbers, ., _ or -."
                );
                return;
            }

            if (password.length() < 8) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password must contain at least 8 characters."
                );
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                        this, "Passwords do not match."
                );
                return;
            }

            boolean registered = AuthService.registerStudent(
                    name, username, password
            );

            if (registered) {
                JOptionPane.showMessageDialog(
                        this,
                        "Student account created successfully. "
                                + "You can now log in."
                );

                dispose();
                new LoginFrame().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "That username already exists. Choose another."
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. Check the database connection.\n"
                            + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            java.util.Arrays.fill(passwordChars, '\0');
            java.util.Arrays.fill(confirmChars, '\0');
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }
}