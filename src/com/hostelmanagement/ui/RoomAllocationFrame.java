package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.AllocationDAO;
import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Allocation;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.util.UIUtils;

public class RoomAllocationFrame extends JFrame {

    private JTable allocationTable;
    private DefaultTableModel tableModel;
    private List<Allocation> currentAllocations;

    public RoomAllocationFrame() {
        setTitle("Room Allocation Management");
        setSize(950, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadAllocationData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Room Allocation — Allotment, Transfers & Vacations");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton allocateBtn = UIUtils.createButton("+ Allocate Room", 130, 30);
        JButton changeRoomBtn = UIUtils.createButton("Change Room", 120, 30);
        JButton vacateBtn = UIUtils.createButton("Vacate Room", 110, 30);
        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);

        actionPanel.add(allocateBtn);
        actionPanel.add(changeRoomBtn);
        actionPanel.add(vacateBtn);
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Student Name", "Roll Number", "Room", "Block / Wing", "Allocated Date", "Vacate Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationTable = new JTable(tableModel);
        UIUtils.styleTable(allocationTable);
        add(new JScrollPane(allocationTable), BorderLayout.CENTER);

        // Listeners
        refreshBtn.addActionListener(e -> loadAllocationData());
        allocateBtn.addActionListener(e -> showAllocateDialog());
        changeRoomBtn.addActionListener(e -> showChangeRoomDialog());
        vacateBtn.addActionListener(e -> performVacateRoom());
    }

    private void loadAllocationData() {
        try {
            currentAllocations = AllocationDAO.getAllAllocations();
            tableModel.setRowCount(0);

            for (Allocation a : currentAllocations) {
                tableModel.addRow(new Object[]{
                        a.getAllocationId(),
                        a.getStudentName(),
                        a.getRollNumber(),
                        a.getRoomNumber(),
                        a.getBlockWing(),
                        a.getAllocationDate(),
                        a.getVacateDate() != null ? a.getVacateDate() : "-",
                        a.getStatus()
                });
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load allocations:\n" + e.getMessage());
        }
    }

    private void showAllocateDialog() {
        try {
            List<Student> allStudents = StudentDAO.getAllStudents();
            List<Student> unallocated = new ArrayList<>();
            for (Student s : allStudents) {
                if (AllocationDAO.getActiveAllocationByStudentId(s.getStudentId()) == null) {
                    unallocated.add(s);
                }
            }

            if (unallocated.isEmpty()) {
                UIUtils.showMessage(this, "All enrolled students already have active room allocations.");
                return;
            }

            List<Room> availableRooms = RoomDAO.getAvailableRooms();
            if (availableRooms.isEmpty()) {
                UIUtils.showMessage(this, "No vacant rooms currently available. Please add rooms or vacate beds first.");
                return;
            }

            JDialog dialog = new JDialog(this, "Allocate Room to Student", true);
            dialog.setSize(450, 240);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));
            form.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

            JComboBox<Student> studentBox = new JComboBox<>(unallocated.toArray(new Student[0]));
            JComboBox<Room> roomBox = new JComboBox<>(availableRooms.toArray(new Room[0]));

            form.add(new JLabel("Select Student:"));
            form.add(studentBox);
            form.add(new JLabel("Select Available Room:"));
            form.add(roomBox);

            dialog.add(form, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            JButton saveBtn = UIUtils.createButton("Confirm Allocation", 150, 32);
            JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
            btnPanel.add(saveBtn);
            btnPanel.add(cancelBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            cancelBtn.addActionListener(e -> dialog.dispose());

            saveBtn.addActionListener(e -> {
                Student s = (Student) studentBox.getSelectedItem();
                Room r = (Room) roomBox.getSelectedItem();
                if (s == null || r == null) return;

                try {
                    boolean ok = AllocationDAO.allocateRoom(s.getStudentId(), r.getRoomId());
                    if (ok) {
                        UIUtils.showMessage(this, "Room " + r.getRoomNumber() + " successfully allocated to " + s.getFullName() + "!");
                        dialog.dispose();
                        loadAllocationData();
                    } else {
                        UIUtils.showError(dialog, "Allocation failed. Check room availability.");
                    }
                } catch (SQLException ex) {
                    UIUtils.showError(dialog, "Database error: " + ex.getMessage());
                }
            });

            dialog.setVisible(true);

        } catch (SQLException ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void showChangeRoomDialog() {
        try {
            List<Allocation> activeList = new ArrayList<>();
            for (Allocation a : currentAllocations) {
                if ("ACTIVE".equalsIgnoreCase(a.getStatus())) {
                    activeList.add(a);
                }
            }

            if (activeList.isEmpty()) {
                UIUtils.showMessage(this, "No active allocations exist to transfer.");
                return;
            }

            List<Room> availableRooms = RoomDAO.getAvailableRooms();
            if (availableRooms.isEmpty()) {
                UIUtils.showMessage(this, "No other rooms with vacant beds currently available.");
                return;
            }

            JDialog dialog = new JDialog(this, "Change / Transfer Room", true);
            dialog.setSize(480, 240);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));
            form.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

            JComboBox<String> studentBox = new JComboBox<>();
            for (Allocation a : activeList) {
                studentBox.addItem(a.getRollNumber() + " - " + a.getStudentName() + " (Current: " + a.getRoomNumber() + ")");
            }

            JComboBox<Room> roomBox = new JComboBox<>(availableRooms.toArray(new Room[0]));

            form.add(new JLabel("Active Resident:"));
            form.add(studentBox);
            form.add(new JLabel("Transfer to Room:"));
            form.add(roomBox);

            dialog.add(form, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            JButton transferBtn = UIUtils.createButton("Confirm Transfer", 140, 32);
            JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
            btnPanel.add(transferBtn);
            btnPanel.add(cancelBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            cancelBtn.addActionListener(e -> dialog.dispose());

            transferBtn.addActionListener(e -> {
                int selectedIdx = studentBox.getSelectedIndex();
                if (selectedIdx < 0) return;
                Allocation alloc = activeList.get(selectedIdx);
                Room targetRoom = (Room) roomBox.getSelectedItem();
                if (targetRoom == null) return;

                try {
                    boolean ok = AllocationDAO.changeRoom(alloc.getStudentId(), targetRoom.getRoomId());
                    if (ok) {
                        UIUtils.showMessage(this, "Student " + alloc.getStudentName() + " successfully transferred to Room " + targetRoom.getRoomNumber() + "!");
                        dialog.dispose();
                        loadAllocationData();
                    } else {
                        UIUtils.showError(dialog, "Transfer failed. Student may already be in this room or room is full.");
                    }
                } catch (SQLException ex) {
                    UIUtils.showError(dialog, "Database error: " + ex.getMessage());
                }
            });

            dialog.setVisible(true);

        } catch (SQLException ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void performVacateRoom() {
        int selectedRow = allocationTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select an active allocation from the table to vacate.");
            return;
        }

        Allocation alloc = currentAllocations.get(selectedRow);

        if (!"ACTIVE".equalsIgnoreCase(alloc.getStatus())) {
            UIUtils.showMessage(this, "This allocation is already " + alloc.getStatus() + ".");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Vacate Room " + alloc.getRoomNumber() + " for student " + alloc.getStudentName() + " (" + alloc.getRollNumber() + ")?\n"
                        + "This will release the bed and mark the allocation as VACATED.",
                "Confirm Vacate",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean vacated = AllocationDAO.vacateRoom(alloc.getAllocationId());
                if (vacated) {
                    UIUtils.showMessage(this, "Room vacated successfully.");
                    loadAllocationData();
                } else {
                    UIUtils.showError(this, "Could not vacate room.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(this, "Database error: " + ex.getMessage());
            }
        }
    }
}
