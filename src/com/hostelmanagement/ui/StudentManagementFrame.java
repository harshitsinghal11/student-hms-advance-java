package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.util.UIUtils;

public class StudentManagementFrame extends JFrame {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Student> currentList;

    public StudentManagementFrame() {
        setTitle("Student Management");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top Search & Add Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Student Management — Records & Profiles");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchField = new JTextField(15);
        JButton searchBtn = UIUtils.createButton("Search", 85, 30);
        JButton resetBtn = UIUtils.createButton("Reset", 75, 30);
        JButton addBtn = UIUtils.createButton("+ Add Student", 120, 30);

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);
        searchPanel.add(addBtn);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Roll Number", "Full Name", "Username", "Course", "Contact", "Emergency", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        UIUtils.styleTable(studentTable);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Bottom Action Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton updateBtn = UIUtils.createButton("Update Selected", 140, 32);
        JButton deleteBtn = UIUtils.createButton("Delete Selected", 130, 32);
        JButton refreshBtn = UIUtils.createButton("Refresh", 90, 32);

        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudentData();
        });
        refreshBtn.addActionListener(e -> loadStudentData());
        addBtn.addActionListener(e -> showAddStudentDialog());
        updateBtn.addActionListener(e -> showUpdateStudentDialog());
        deleteBtn.addActionListener(e -> performDeleteStudent());
    }

    private void loadStudentData() {
        try {
            currentList = StudentDAO.getAllStudents();
            renderTableData(currentList);
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load student data:\n" + e.getMessage());
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStudentData();
            return;
        }
        try {
            currentList = StudentDAO.searchStudents(keyword);
            renderTableData(currentList);
        } catch (SQLException e) {
            UIUtils.showError(this, "Search failed:\n" + e.getMessage());
        }
    }

    private void renderTableData(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getRollNumber(),
                    s.getFullName(),
                    s.getUsername(),
                    s.getCourse(),
                    s.getContactNumber(),
                    s.getEmergencyContact(),
                    s.getAddress()
            });
        }
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nameFld = new JTextField();
        JTextField userFld = new JTextField();
        JPasswordField passFld = new JPasswordField();
        JTextField rollFld = new JTextField();
        JTextField courseFld = new JTextField();
        JTextField contactFld = new JTextField();
        JTextField emergFld = new JTextField();
        JTextField addrFld = new JTextField();

        form.add(new JLabel("Full Name:"));
        form.add(nameFld);
        form.add(new JLabel("Username:"));
        form.add(userFld);
        form.add(new JLabel("Password (min 8):"));
        form.add(passFld);
        form.add(new JLabel("Roll Number:"));
        form.add(rollFld);
        form.add(new JLabel("Course:"));
        form.add(courseFld);
        form.add(new JLabel("Contact Number:"));
        form.add(contactFld);
        form.add(new JLabel("Emergency Contact:"));
        form.add(emergFld);
        form.add(new JLabel("Address:"));
        form.add(addrFld);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Save Student", 120, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name = nameFld.getText().trim();
            String username = userFld.getText().trim();
            String password = new String(passFld.getPassword());
            String roll = rollFld.getText().trim();
            String course = courseFld.getText().trim();
            String contact = contactFld.getText().trim();
            String emerg = emergFld.getText().trim();
            String addr = addrFld.getText().trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || roll.isEmpty() || course.isEmpty() || contact.isEmpty()) {
                UIUtils.showError(dialog, "Please fill in all required fields.");
                return;
            }

            if (password.length() < 8) {
                UIUtils.showError(dialog, "Password must be at least 8 characters long.");
                return;
            }

            try {
                boolean success = StudentDAO.addStudentWithAccount(name, username, password, roll, course, contact, emerg, addr);
                if (success) {
                    UIUtils.showMessage(this, "Student added successfully!");
                    dialog.dispose();
                    loadStudentData();
                } else {
                    UIUtils.showError(dialog, "Could not add student. Check username / roll number uniqueness.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void showUpdateStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a student from the table to update.");
            return;
        }

        Student student = currentList.get(selectedRow);

        JDialog dialog = new JDialog(this, "Update Student - " + student.getRollNumber(), true);
        dialog.setSize(420, 390);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nameFld = new JTextField(student.getFullName());
        JTextField rollFld = new JTextField(student.getRollNumber());
        JTextField courseFld = new JTextField(student.getCourse());
        JTextField contactFld = new JTextField(student.getContactNumber());
        JTextField emergFld = new JTextField(student.getEmergencyContact());
        JTextField addrFld = new JTextField(student.getAddress());

        form.add(new JLabel("Full Name:"));
        form.add(nameFld);
        form.add(new JLabel("Roll Number:"));
        form.add(rollFld);
        form.add(new JLabel("Course:"));
        form.add(courseFld);
        form.add(new JLabel("Contact Number:"));
        form.add(contactFld);
        form.add(new JLabel("Emergency Contact:"));
        form.add(emergFld);
        form.add(new JLabel("Address:"));
        form.add(addrFld);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton updateBtn = UIUtils.createButton("Save Changes", 130, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(updateBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        updateBtn.addActionListener(e -> {
            String name = nameFld.getText().trim();
            String roll = rollFld.getText().trim();
            String course = courseFld.getText().trim();
            String contact = contactFld.getText().trim();
            String emerg = emergFld.getText().trim();
            String addr = addrFld.getText().trim();

            if (name.isEmpty() || roll.isEmpty() || course.isEmpty() || contact.isEmpty()) {
                UIUtils.showError(dialog, "Name, Roll Number, Course, and Contact are required.");
                return;
            }

            try {
                boolean ok = StudentDAO.updateStudent(student.getStudentId(), name, roll, course, contact, emerg, addr);
                if (ok) {
                    UIUtils.showMessage(this, "Student updated successfully!");
                    dialog.dispose();
                    loadStudentData();
                } else {
                    UIUtils.showError(dialog, "Update failed.");
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062 && "23000".equals(ex.getSQLState())) {
                    UIUtils.showError(dialog, "Roll number '" + roll + "' is already assigned to another student.");
                } else {
                    UIUtils.showError(dialog, "Error: " + ex.getMessage());
                }
            }
        });

        dialog.setVisible(true);
    }

    private void performDeleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a student from the table to delete.");
            return;
        }

        Student student = currentList.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete student: " + student.getFullName() + " (" + student.getRollNumber() + ")?\n"
                        + "This will also remove their room allocation, fees, and complaints records.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = StudentDAO.deleteStudent(student.getStudentId());
                if (deleted) {
                    UIUtils.showMessage(this, "Student deleted successfully.");
                    loadStudentData();
                } else {
                    UIUtils.showError(this, "Could not delete student.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(this, "Database error: " + ex.getMessage());
            }
        }
    }
}
