package screens;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GameAction {
    private static JTextField[][] textFields;

    public GameAction(JTextField[][] tfs) {
        textFields = tfs;
    }

    public static JPanel init() {
        JPanel panel = new JPanel();

        panel.setSize(600, 400);
        panel.setLayout(new GridLayout(2, 1));

        JPanel topPanel = new JPanel(new GridLayout(1, 2));

        JPanel greenPanel = new JPanel(new BorderLayout());
        greenPanel.setBorder(new LineBorder(Color.GRAY, 2));
        greenPanel.setBackground(Color.GREEN);
        JLabel greenLabel = new JLabel("GREEN TEAM", SwingConstants.CENTER);
        greenLabel.setFont(new Font("Arial", Font.BOLD, 25));
        greenLabel.setForeground(Color.GREEN);
        JPanel greenLabelPanel = new JPanel();
        greenLabelPanel.setBackground(Color.BLACK);
        greenLabelPanel.add(greenLabel);
        greenPanel.add(greenLabelPanel, BorderLayout.NORTH);
        JPanel greenListPanel = new JPanel(new GridLayout(15, 1));
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if (!playerText.isEmpty()) {
                JLabel playerLabel = new JLabel(playerText, SwingConstants.CENTER);
                playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                greenListPanel.add(playerLabel);
            }
        }
        greenPanel.add(greenListPanel, BorderLayout.CENTER);

        JPanel redPanel = new JPanel(new BorderLayout());
        redPanel.setBorder(new LineBorder(Color.GRAY, 2));
        redPanel.setBackground(Color.PINK);
        JLabel redLabel = new JLabel("RED TEAM", SwingConstants.CENTER);
        redLabel.setFont(new Font("Arial", Font.BOLD, 25));
        redLabel.setForeground(Color.RED);
        JPanel redLabelPanel = new JPanel();
        redLabelPanel.setBackground(Color.BLACK);
        redLabelPanel.add(redLabel);
        redPanel.add(redLabelPanel, BorderLayout.NORTH);
        JPanel redListPanel = new JPanel(new GridLayout(15, 1));
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][1]);
            if (!playerText.isEmpty()) {
                JLabel playerLabel = new JLabel(playerText, SwingConstants.CENTER);
                playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                redListPanel.add(playerLabel);
            }
        }
        redPanel.add(redListPanel, BorderLayout.CENTER);

        topPanel.add(greenPanel);
        topPanel.add(redPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(new LineBorder(Color.GRAY, 2));

        panel.add(topPanel);
        panel.add(bottomPanel);

        return topPanel;
    }

    private static String getPlayerText(JTextField field) {
        String name = field.getText().trim();
        return name.isEmpty() ? "" : name + " - 0 pts";
    }
}
