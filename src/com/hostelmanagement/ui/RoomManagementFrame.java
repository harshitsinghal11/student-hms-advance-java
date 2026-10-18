package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.util.UIUtils;

public class RoomManagementFrame extends JFrame {

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterBox;
    private List<Room> currentRooms;

    public RoomManagementFrame() {
        setTitle("Room Management");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadRoomData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top Filter & Action Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.createTitleLabel("Room Management — Inventory & Capacities");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        filterBox = new JComboBox<>(new String[]{"All Rooms", "Available Rooms Only", "Occupied Rooms Only"});
        JButton addRoomBtn = UIUtils.createButton("+ Add Room", 110, 30);
        JButton refreshBtn = UIUtils.createButton("Refresh", 80, 30);

        actionPanel.add(new JLabel("Filter:"));
        actionPanel.add(filterBox);
        actionPanel.add(addRoomBtn);
        actionPanel.add(refreshBtn);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Room Number", "Block / Wing", "Capacity", "Occupied Beds", "Available Beds", "Monthly Fee (₹)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        UIUtils.styleTable(roomTable);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton updateBtn = UIUtils.createButton("Edit Room", 110, 32);
        JButton deleteBtn = UIUtils.createButton("Delete Room", 120, 32);

        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        filterBox.addActionListener(e -> loadRoomData());
        refreshBtn.addActionListener(e -> loadRoomData());
        addRoomBtn.addActionListener(e -> showAddRoomDialog());
        updateBtn.addActionListener(e -> showEditRoomDialog());
        deleteBtn.addActionListener(e -> performDeleteRoom());
    }

    private void loadRoomData() {
        try {
            int selectedIndex = filterBox.getSelectedIndex();
            if (selectedIndex == 1) {
                currentRooms = RoomDAO.getAvailableRooms();
            } else if (selectedIndex == 2) {
                currentRooms = RoomDAO.getOccupiedRooms();
            } else {
                currentRooms = RoomDAO.getAllRooms();
            }

            tableModel.setRowCount(0);
            for (Room r : currentRooms) {
                tableModel.addRow(new Object[]{
                        r.getRoomId(),
                        r.getRoomNumber(),
                        r.getBlockWing(),
                        r.getCapacity(),
                        r.getCurrentOccupancy(),
                        r.getAvailableBeds(),
                        String.format("%.2f", r.getMonthlyFee()),
                        r.getRoomStatus()
                });
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load rooms:\n" + e.getMessage());
        }
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Add New Room", true);
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField roomNoFld = new JTextField();
        JTextField blockFld = new JTextField();
        JTextField capacityFld = new JTextField("2");
        JTextField feeFld = new JTextField("5000.00");

        form.add(new JLabel("Room Number:"));
        form.add(roomNoFld);
        form.add(new JLabel("Block / Wing:"));
        form.add(blockFld);
        form.add(new JLabel("Bed Capacity:"));
        form.add(capacityFld);
        form.add(new JLabel("Monthly Fee (₹):"));
        form.add(feeFld);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Save Room", 110, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String roomNo = roomNoFld.getText().trim();
            String block = blockFld.getText().trim();
            String capStr = capacityFld.getText().trim();
            String feeStr = feeFld.getText().trim();

            if (roomNo.isEmpty() || block.isEmpty()) {
                UIUtils.showError(dialog, "Room number and block are required.");
                return;
            }

            try {
                int capacity = Integer.parseInt(capStr);
                double fee = Double.parseDouble(feeStr);

                if (capacity <= 0 || fee < 0) {
                    UIUtils.showError(dialog, "Capacity must be positive and fee non-negative.");
                    return;
                }

                boolean added = RoomDAO.addRoom(roomNo, block, capacity, fee);
                if (added) {
                    UIUtils.showMessage(this, "Room added successfully!");
                    dialog.dispose();
                    loadRoomData();
                } else {
                    UIUtils.showError(dialog, "Room number already exists.");
                }
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialog, "Please enter valid numeric capacity and fee values.");
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void showEditRoomDialog() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a room to edit.");
            return;
        }

        Room room = currentRooms.get(selectedRow);

        JDialog dialog = new JDialog(this, "Edit Room " + room.getRoomNumber(), true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField roomNoFld = new JTextField(room.getRoomNumber());
        JTextField blockFld = new JTextField(room.getBlockWing());
        JTextField capacityFld = new JTextField(String.valueOf(room.getCapacity()));
        JTextField feeFld = new JTextField(String.valueOf(room.getMonthlyFee()));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"AVAILABLE", "FULL", "MAINTENANCE"});
        statusBox.setSelectedItem(room.getRoomStatus());

        form.add(new JLabel("Room Number:"));
        form.add(roomNoFld);
        form.add(new JLabel("Block / Wing:"));
        form.add(blockFld);
        form.add(new JLabel("Capacity:"));
        form.add(capacityFld);
        form.add(new JLabel("Monthly Fee (₹):"));
        form.add(feeFld);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIUtils.createButton("Save Changes", 120, 32);
        JButton cancelBtn = UIUtils.createButton("Cancel", 80, 32);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String roomNo = roomNoFld.getText().trim();
                String block = blockFld.getText().trim();
                int capacity = Integer.parseInt(capacityFld.getText().trim());
                double fee = Double.parseDouble(feeFld.getText().trim());
                String status = (String) statusBox.getSelectedItem();

                if (capacity < room.getCurrentOccupancy()) {
                    UIUtils.showError(dialog, "Capacity cannot be less than current occupancy (" + room.getCurrentOccupancy() + ").");
                    return;
                }

                boolean ok = RoomDAO.updateRoom(room.getRoomId(), roomNo, block, capacity, fee, status);
                if (ok) {
                    UIUtils.showMessage(this, "Room updated successfully!");
                    dialog.dispose();
                    loadRoomData();
                } else {
                    UIUtils.showError(dialog, "Failed to update room.");
                }
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialog, "Invalid number format for capacity or fee.");
            } catch (SQLException ex) {
                UIUtils.showError(dialog, "Database error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void performDeleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showMessage(this, "Please select a room to delete.");
            return;
        }

        Room room = currentRooms.get(selectedRow);

        if (room.getCurrentOccupancy() > 0) {
            UIUtils.showError(this, "Cannot delete room " + room.getRoomNumber() + " because it currently has students allocated to it.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete Room " + room.getRoomNumber() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = RoomDAO.deleteRoom(room.getRoomId());
                if (deleted) {
                    UIUtils.showMessage(this, "Room deleted successfully.");
                    loadRoomData();
                } else {
                    UIUtils.showError(this, "Could not delete room. Active allocations may exist.");
                }
            } catch (SQLException ex) {
                UIUtils.showError(this, "Database error: " + ex.getMessage());
            }
        }
    }
}
