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

    public void run() {
        JFrame frame = new JFrame("Player Entry Screen");
        frame.setSize(400, 600);
        frame.setLayout(new GridLayout(19, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel left = new JLabel("GREEN TEAM", SwingConstants.CENTER);
        left.setOpaque(true);
        left.setBackground(Color.GREEN);
        JLabel right = new JLabel("RED TEAM", SwingConstants.CENTER);
        right.setOpaque(true);
        right.setBackground(Color.PINK);

        frame.add(left);
        frame.add(right);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                textFields[i][j] = new JTextField();
                textFields[i][j].setBackground(j == 0 ? Color.GREEN : Color.PINK);
                frame.add(textFields[i][j]);
            }
        }

        JTextField playerIDField = new JTextField();
        JButton submitButton = new JButton("Enter Player ID");
        JButton startCountdownButton = new JButton("Start Countdown (F5)");
        JButton clearFieldsButton = new JButton("Clear Fields (F12)");

        frame.add(playerIDField);
        frame.add(submitButton);
        frame.add(startCountdownButton);
        frame.add(clearFieldsButton);

        submitButton.addActionListener(e -> {
            String playerIDText = playerIDField.getText().trim();
            if (!playerIDText.matches("\\d+")) {
                JOptionPane.showMessageDialog(frame, "Invalid Player ID! Must be a number.");
                return;
            }
            int playerID = Integer.parseInt(playerIDText);
            processPlayerID(playerID);
        });

        startCountdownButton.addActionListener(e -> Countdown.run(frame, textFields));
        clearFieldsButton.addActionListener(e -> Util.clearTextFields(textFields));

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "startCountdown");
        frame.getRootPane().getActionMap()
                .put("startCountdown", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Countdown.run(frame, textFields);
                    }
                });

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "clear");
        frame.getRootPane().getActionMap()
                .put("clear", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Util.clearTextFields(textFields);
                    }
                });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
