package com.hostelmanagement;

import javax.swing.SwingUtilities;

import com.hostelmanagement.ui.LoginFrame;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

        });
    }
}