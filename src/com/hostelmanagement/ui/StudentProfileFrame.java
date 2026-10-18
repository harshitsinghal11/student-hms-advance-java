package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentProfileFrame extends JFrame {

    private Student currentStudent;
    private JTextField nameFld;
    private JTextField userFld;
    private JTextField rollFld;
    private JTextField courseFld;
    private JTextField contactFld;
    private JTextField emergFld;
    private JTextField addrFld;

    public StudentProfileFrame() {
        setTitle("Student Profile");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadProfileData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = UIUtils.createTitleLabel("My Student Profile");
        add(titleLabel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        nameFld = new JTextField();
        userFld = new JTextField();
        userFld.setEditable(false);
        rollFld = new JTextField();
        rollFld.setEditable(false);
        courseFld = new JTextField();
        courseFld.setEditable(false);
        contactFld = new JTextField();
        emergFld = new JTextField();
        addrFld = new JTextField();

        form.add(new JLabel("Full Name:"));
        form.add(nameFld);
        form.add(new JLabel("Username:"));
        form.add(userFld);
        form.add(new JLabel("Roll Number:"));
        form.add(rollFld);
        form.add(new JLabel("Course:"));
        form.add(courseFld);
        form.add(new JLabel("Contact Number:"));
        form.add(contactFld);
        form.add(new JLabel("Emergency Contact:"));
        form.add(emergFld);
        form.add(new JLabel("Home Address:"));
        form.add(addrFld);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Save Profile", 130, 32);
        JButton closeBtn = UIUtils.createButton("Close", 80, 32);

        btnPanel.add(saveBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> performSaveProfile());
    }

    private void loadProfileData() {
        User user = Session.getCurrentUser();
        if (user == null) {
            UIUtils.showError(this, "No active session.");
            dispose();
            return;
        }

        try {
            currentStudent = StudentDAO.getStudentByUserId(user.getUserId());
            if (currentStudent != null) {
                nameFld.setText(currentStudent.getFullName());
                userFld.setText(currentStudent.getUsername());
                rollFld.setText(currentStudent.getRollNumber());
                courseFld.setText(currentStudent.getCourse());
                contactFld.setText(currentStudent.getContactNumber());
                emergFld.setText(currentStudent.getEmergencyContact());
                addrFld.setText(currentStudent.getAddress() != null ? currentStudent.getAddress() : "");
            } else {
                nameFld.setText(user.getFullName());
                userFld.setText(user.getUsername());
                rollFld.setText("Pending Allotment");
                courseFld.setText("Pending");
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load profile:\n" + e.getMessage());
        }
    }

    private void performSaveProfile() {
        if (currentStudent == null) {
            UIUtils.showMessage(this, "Profile not yet initialized by hostel administration.");
            return;
        }

        String name = nameFld.getText().trim();
        String contact = contactFld.getText().trim();
        String emerg = emergFld.getText().trim();
        String addr = addrFld.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            UIUtils.showError(this, "Name and Contact number cannot be empty.");
            return;
        }

        try {
            boolean ok = StudentDAO.updateStudent(
                    currentStudent.getStudentId(),
                    name,
                    currentStudent.getCourse(),
                    contact,
                    emerg,
                    addr
            );

            if (ok) {
                UIUtils.showMessage(this, "Profile updated successfully!");
                loadProfileData();
            } else {
                UIUtils.showError(this, "Failed to update profile.");
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Database error: " + e.getMessage());
        }
    }
}
