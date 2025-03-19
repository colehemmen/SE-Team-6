package screens;

import database.DatabaseConnection;
import udp.UDPClient;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PlayerEntry {

    static DatabaseConnection databaseConnection;
    static UDPClient udpClient;

    static JTextField[][] textFields;

    public PlayerEntry(DatabaseConnection db, UDPClient udp, JTextField[][] tfs) {
        databaseConnection = db;
        udpClient = udp;

        textFields = tfs;
    }

    public static JPanel run(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(600, 800));
        panel.setLayout(new GridLayout(19, 2));
        panel.setBackground(Color.WHITE);

        JLabel left = new JLabel("GREEN TEAM", SwingConstants.CENTER);
        left.setOpaque(true);
        left.setBackground(Color.GREEN);
        JLabel right = new JLabel("RED TEAM", SwingConstants.CENTER);
        right.setOpaque(true);
        right.setBackground(Color.PINK);

        panel.add(left);
        panel.add(right);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                JTextField field = new JTextField();
                field.setEditable(false);
                field.setOpaque(true);
                field.setBackground(j == 0 ? Color.GREEN : Color.PINK);

                textFields[i][j] = field;
                panel.add(textFields[i][j]);
            }
        }

        JTextField playerIDField = new JTextField();
        playerIDField.grabFocus();

        JButton submitButton = new JButton("Enter Player ID");
        JButton startCountdownButton = new JButton("Start Countdown (F5)");
        JButton clearFieldsButton = new JButton("Clear Fields (F12)");

        panel.add(playerIDField);
        panel.add(submitButton);
        panel.add(startCountdownButton);
        panel.add(clearFieldsButton);

        registerEvents(startCountdownButton, clearFieldsButton, submitButton, playerIDField, cardPanel);

        return panel;
    }

    private static void registerEvents(JButton startCountdownButton, JButton clearFieldsButton, JButton submitButton, JTextField playerIDField, JPanel cardPanel) {
        startCountdownButton.addActionListener(e -> Countdown.run(textFields));
        clearFieldsButton.addActionListener(e -> Util.clearTextFields(textFields));

        submitButton.addActionListener(e -> {
            String playerIDText = playerIDField.getText().trim();
            if (!playerIDText.matches("\\d+")) {
                JOptionPane.showMessageDialog(cardPanel, "Invalid Player ID! Must be a number.");
                return;
            }
            int playerID = Integer.parseInt(playerIDText);
            processPlayerID(playerID);
        });


        cardPanel.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "startCountdown");
        cardPanel.getRootPane().getActionMap()
                .put("startCountdown", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Countdown.run(textFields);
                    }
                });

        cardPanel.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "clear");
        cardPanel.getRootPane().getActionMap()
                .put("clear", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Util.clearTextFields(textFields);
                    }
                });
    }

    private static void processPlayerID(int playerID) {
        String existingCodename = databaseConnection.getCodenameByPlayerId(playerID);

        if(existingCodename != null) {
            JOptionPane.showMessageDialog(null, "Welcome back " + existingCodename);
            Util.writeToScreen(textFields, existingCodename);
        } else {
            String newCodename = JOptionPane.showInputDialog("New player! Enter your codename:");
            if (Util.isValidInput(newCodename)) {
                databaseConnection.createNewPlayerByIdAndCodename(playerID, newCodename);
            }

            String newEquipmentId = JOptionPane.showInputDialog("Enter your equipment id:");
            if (Util.isValidInput(newEquipmentId)) {
                udpClient.transmitEquipmentCode(newEquipmentId);
            }

            JOptionPane.showMessageDialog(null, "New user saved successfully!");
            Util.writeToScreen(textFields, newCodename);
        }
    }
}
