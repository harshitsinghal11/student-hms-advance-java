package com.hostelmanagement.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.*;

import com.hostelmanagement.util.UIUtils;

public class ModuleFrame extends JFrame {

    private String moduleName;

    public ModuleFrame(String moduleName, String[] functions) {

        this.moduleName = moduleName;

        setTitle(moduleName);

        setSize(500, 400);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        initializeUI(functions);
    }

    private void initializeUI(String[] functions) {

        JLabel titleLabel =
                new JLabel(moduleName);

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        );

        add(titleLabel, BorderLayout.NORTH);

        JPanel functionPanel =
                new JPanel(
                        new GridLayout(
                                functions.length,
                                1,
                                10,
                                10
                        )
                );

        functionPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        50,
                        20,
                        50
                )
        );

        for (String function : functions) {

            JButton button =
                    UIUtils.createButton(function);

            functionPanel.add(button);

            button.addActionListener(e -> {

                UIUtils.showMessage(
                        this,
                        function
                                + "\n\nThis functionality will be implemented in Phase 2."
                );

            });
        }

        add(functionPanel, BorderLayout.CENTER);
    }
}