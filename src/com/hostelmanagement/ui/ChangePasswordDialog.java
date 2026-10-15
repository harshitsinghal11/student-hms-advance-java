package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.hostelmanagement.auth.AuthService;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class ChangePasswordDialog extends JDialog {

    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ChangePasswordDialog(Frame parent) {
        super(parent, "Change Password", true);
        setSize(420, 260);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        currentPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        formPanel.add(new JLabel("Current Password:"));
        formPanel.add(currentPasswordField);

        formPanel.add(new JLabel("New Password:"));
        formPanel.add(newPasswordField);

        formPanel.add(new JLabel("Confirm New Password:"));
        formPanel.add(confirmPasswordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        JButton updateButton = UIUtils.createButton("Update Password");
        updateButton.setPreferredSize(new java.awt.Dimension(150, 32));

        JButton cancelButton = UIUtils.createButton("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 32));

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> handleChangePassword());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleChangePassword() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No active session found. Please log in again.",
                    "Session Expired",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        char[] currentChars = currentPasswordField.getPassword();
        char[] newChars = newPasswordField.getPassword();
        char[] confirmChars = confirmPasswordField.getPassword();

        String currentPassword = new String(currentChars);
        String newPassword = new String(newChars);
        String confirmPassword = new String(confirmChars);

        try {
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "All fields are required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int minLength = "ADMIN".equalsIgnoreCase(currentUser.getRole()) ? 12 : 8;
            if (newPassword.length() < minLength) {
                JOptionPane.showMessageDialog(
                        this,
                        "New password must be at least " + minLength + " characters long.",
                        "Weak Password",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                        this,
                        "New password and confirmation do not match.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (newPassword.equals(currentPassword)) {
                JOptionPane.showMessageDialog(
                        this,
                        "New password must be different from current password.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean updated = AuthService.changePassword(
                    currentUser.getUserId(),
                    currentPassword,
                    newPassword
            );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password changed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Current password is incorrect.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while updating password:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(currentChars, '\0');
            Arrays.fill(newChars, '\0');
            Arrays.fill(confirmChars, '\0');
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }
}
