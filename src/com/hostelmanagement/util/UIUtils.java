package com.hostelmanagement.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 35));

        return button;
    }

    public static void showMessage(Component parent, String message) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Student Hostel Management System",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}