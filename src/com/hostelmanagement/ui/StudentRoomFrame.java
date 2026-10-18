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

import com.hostelmanagement.dao.AllocationDAO;
import com.hostelmanagement.dao.RoomDAO;
import com.hostelmanagement.dao.StudentDAO;
import com.hostelmanagement.model.Allocation;
import com.hostelmanagement.model.Room;
import com.hostelmanagement.model.Student;
import com.hostelmanagement.model.User;
import com.hostelmanagement.session.Session;
import com.hostelmanagement.util.UIUtils;

public class StudentRoomFrame extends JFrame {

    public StudentRoomFrame() {
        setTitle("My Allocated Room Details");
        setSize(480, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = UIUtils.createTitleLabel("Hostel Accommodation — Room Details");
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 10, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        User user = Session.getCurrentUser();
        if (user == null) {
            UIUtils.showError(this, "No active session.");
            dispose();
            return;
        }

        try {
            Student student = StudentDAO.getStudentByUserId(user.getUserId());
            if (student == null) {
                contentPanel.add(new JLabel("Status:"));
                contentPanel.add(new JLabel("Student profile pending initialization."));
            } else {
                Allocation alloc = AllocationDAO.getActiveAllocationByStudentId(student.getStudentId());
                if (alloc == null) {
                    contentPanel.add(new JLabel("Status:"));
                    contentPanel.add(new JLabel("No active room allocation assigned yet."));
                    contentPanel.add(new JLabel("Action:"));
                    contentPanel.add(new JLabel("Please contact the Chief Warden / Hostel Office."));
                } else {
                    Room room = RoomDAO.getRoomById(alloc.getRoomId());

                    contentPanel.add(new JLabel("Allocated Room:"));
                    JTextField roomFld = new JTextField(alloc.getRoomNumber());
                    roomFld.setEditable(false);
                    contentPanel.add(roomFld);

                    contentPanel.add(new JLabel("Block / Wing:"));
                    JTextField blockFld = new JTextField(alloc.getBlockWing());
                    blockFld.setEditable(false);
                    contentPanel.add(blockFld);

                    contentPanel.add(new JLabel("Room Capacity:"));
                    JTextField capFld = new JTextField(room != null ? room.getCapacity() + " Beds" : "N/A");
                    capFld.setEditable(false);
                    contentPanel.add(capFld);

                    contentPanel.add(new JLabel("Current Occupancy:"));
                    JTextField occFld = new JTextField(room != null ? room.getCurrentOccupancy() + " Students" : "N/A");
                    occFld.setEditable(false);
                    contentPanel.add(occFld);

                    contentPanel.add(new JLabel("Monthly Rent:"));
                    JTextField feeFld = new JTextField(room != null ? String.format("₹ %.2f", room.getMonthlyFee()) : "N/A");
                    feeFld.setEditable(false);
                    contentPanel.add(feeFld);

                    contentPanel.add(new JLabel("Allotment Date:"));
                    JTextField dateFld = new JTextField(alloc.getAllocationDate().toString());
                    dateFld.setEditable(false);
                    contentPanel.add(dateFld);
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Failed to load room details:\n" + e.getMessage());
        }

        add(contentPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton closeBtn = UIUtils.createButton("Close", 90, 32);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());
    }
}
