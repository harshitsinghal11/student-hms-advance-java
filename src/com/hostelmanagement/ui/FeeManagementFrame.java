package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.FeeDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Fee;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.util.UIUtils;

public class FeeManagementFrame extends JFrame {

    private JTable feeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterBox;
    private List<Fee> currentFees;

    public FeeManagementFrame() {
        setTitle("Fee Management");
        setSize(980, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadFeeData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Fee Management — Dues, Collections & Receipts");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        filterBox = new JComboBox<>(new String[]{"All Fee Records", "Pending / Overdue Fees Only"});
        JButton recordFeeBtn = UIUtils.createButton("+ Record Fee", 120, 30);
        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);

        actionPanel.add(new JLabel("Filter:"));
        actionPanel.add(filterBox);
        actionPanel.add(recordFeeBtn);
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Student Name", "Roll Number", "Amount Due (₹)", "Amount Paid (₹)", "Pending Due (₹)", "Due Date", "Payment Date", "Status", "Receipt / Ref"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feeTable = new JTable(tableModel);
        UIUtils.styleTable(feeTable);
        add(new JScrollPane(feeTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton recordPaymentBtn = UIUtils.createButton("Record Payment", 130, 32);
        bottomPanel.add(recordPaymentBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        filterBox.addActionListener(e -> loadFeeData());
        refreshBtn.addActionListener(e -> loadFeeData());
        recordFeeBtn.addActionListener(e -> showRecordFeeDialog());
        recordPaymentBtn.addActionListener(e -> showRecordPaymentDialog());
    }

    private void loadFeeData() {
        try {
            if (filterBox.getSelectedIndex() == 1) {
                currentFees = FeeDAO.getPendingFees();
            } else {
                currentFees = FeeDAO.getAllFees();
            }

            tableModel.setRowCount(0);
            for (Fee f : currentFees) {
                tableModel.addRow(new Object[]{
                        f.getFeeId(),
                        f.getStudentName(),
                        f.getRollNumber(),
                        String.format("%.2f", f.getAmountDue()),
                        String.format("%.2f", f.getAmountPaid()),
                        String.format("%.2f", f.getPendingAmount()),
                        f.getDueDate(),
                        f.getPaymentDate() != null ? f.getPaymentDate() : "-",
                        f.getPaymentStatus(),
                        f.getTransactionRef() != null ? f.getTransactionRef() : "-"
                });
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load fees:\n" + e.getMessage());
        }
    }

    private void showRecordFeeDialog() {
        try {
            List<Student> students = StudentDAO.getAllStudents();
            if (students.isEmpty()) {
                UIUtils.showMessage(this, "No enrolled students found. Add a student first.");
                return;
            }

            JDialog dialog = new JDialog(this, "Issue / Record Fee Invoice", true);
            dialog.setSize(420, 260);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(3, 2, 8, 12));
            form.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

            JComboBox<Student> studentBox = new JComboBox<>(students.toArray(new Student[0]));
            JTextField amountFld = new JTextField("6000.00");
            JTextField dueDateFld = new JTextField(LocalDate.now().plusMonths(1).toString());

            form.add(new JLabel("Select Student:"));
            form.add(studentBox);
            form.add(new JLabel("Amount Due (₹):"));
            form.add(amountFld);
            form.add(new JLabel("Due Date (YYYY-MM-DD):"));
            form.add(dueDateFld);

            dialog.add(form, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            JButton saveBtn = UIUtils.createButton("Issue Invoice", 120, 32);
            JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
            btnPanel.add(saveBtn);
            btnPanel.add(cancelBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            cancelBtn.addActionListener(e -> dialog.dispose());

            saveBtn.addActionListener(e -> {
                Student s = (Student) studentBox.getSelectedItem();
                String amtStr = amountFld.getText().trim();
                String dateStr = dueDateFld.getText().trim();

                try {
                    double amount = Double.parseDouble(amtStr);
                    Date dueDate = Date.valueOf(dateStr);

                    if (amount <= 0) {
                        UIUtils.showError(dialog, "Amount must be greater than zero.");
                        return;
                    }

                    boolean ok = FeeDAO.recordFee(s.getStudentId(), amount, dueDate);
                    if (ok) {
                        UIUtils.showMessage(this, "Fee invoice recorded for " + s.getFullName() + "!");
                        dialog.dispose();
                        loadFeeData();
                    } else {
                        UIUtils.showError(dialog, "Could not record fee.");
                    }
                } catch (NumberFormatException ex) {
                    UIUtils.showError(dialog, "Invalid amount format.");
                } catch (IllegalArgumentException ex) {
                    UIUtils.showError(dialog, "Invalid date format. Use YYYY-MM-DD.");
                } catch (SQLException ex) {
                    UIUtils.showError(dialog, "Database error: " + ex.getMessage());
                }
            });

            dialog.setVisible(true);

        } catch (SQLException ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void showRecordPaymentDialog() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a fee record from the table to record payment.");
            return;
        }

        Fee fee = currentFees.get(selectedRow);

        if ("PAID".equalsIgnoreCase(fee.getPaymentStatus())) {
            UIUtils.showMessage(this, "This fee is already fully paid.");
            return;
        }

        JDialog dialog = new JDialog(this, "Record Fee Payment - " + fee.getStudentName(), true);
        dialog.setSize(420, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JTextField dueDisplay = new JTextField(String.format("₹ %.2f", fee.getPendingAmount()));
        dueDisplay.setEditable(false);
        JTextField paymentFld = new JTextField(String.valueOf(fee.getPendingAmount()));
        JTextField refFld = new JTextField("REC-" + System.currentTimeMillis() % 100000);

        form.add(new JLabel("Pending Due:"));
        form.add(dueDisplay);
        form.add(new JLabel("Payment Amount (₹):"));
        form.add(paymentFld);
        form.add(new JLabel("Transaction / Receipt Ref:"));
        form.add(refFld);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Confirm Payment", 140, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                double payAmount = Double.parseDouble(paymentFld.getText().trim());
                String ref = refFld.getText().trim();

                if (payAmount <= 0) {
                    UIUtils.showError(dialog, "Payment amount must be greater than zero.");
                    return;
                }

                boolean ok = FeeDAO.recordPayment(fee.getFeeId(), payAmount, ref);
                if (ok) {
                    UIUtils.showMessage(this, "Payment successfully recorded!");
                    dialog.dispose();
                    loadFeeData();
                } else {
                    UIUtils.showError(dialog, "Failed to record payment.");
                }
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialog, "Please enter a valid numeric payment amount.");
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}
