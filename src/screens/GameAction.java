package screens;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Objects;

public class GameAction {
    private static JTextField[][] textFields;

    private static JPanel mainPanel;
    private static JLabel timerLabel; 
    private static int timeRemaining = 360; 

    public GameAction(JTextField[][] tfs) {
        textFields = tfs;

        JPanel rootPanel = new JPanel(new GridLayout(1, 3));
        rootPanel.setName("root");

        JPanel greenPanel = buildGreenTeamPanel();
        JPanel redPanel = buildRedTeamPanel();

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(new LineBorder(Color.GRAY, 2));

        rootPanel.add(greenPanel);
        rootPanel.add(buildTimerPanel());
        rootPanel.add(redPanel);

        mainPanel = rootPanel;
    }

    public static void run() {
        startTimer();
    }

    public static void updateTextValues(JTextField[][] tfs) {
        textFields = tfs;

        buildGreenTeamListPanel(Objects.requireNonNull(findPanelByName("green-panel")));
        buildRedTeamListPanel(Objects.requireNonNull(findPanelByName("red-panel")));

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void processEvent(String event) {
       // TODO: Process hits and display them on the screen
        if (event == null || event.isEmpty()) {
            System.out.println("Received an empty event, skipping...");
            return;
        }

        String[] parts = event.split(":");
        if (parts.length < 2) {
            System.out.println("Malformed event: " + event);
            return;
        }

        String attackerId = parts[0];
        String targetId = parts[1];

        if(Objects.equals(attackerId, "43")) {
            // Green base has been tagged
        } else if (Objects.equals(attackerId, "53")) {
            // Red base has been tagged
        } else {
            // Player was tagged
        }
    }

    public static JPanel getMainPanel() {
        return mainPanel;
    }

    private static JPanel findPanelByName(String name) {
        for (Component comp : Objects.requireNonNull(mainPanel.getComponents())) {
            if (comp instanceof JPanel && name.equals(comp.getName())) {
               return (JPanel) comp;
            }
        }

        return null;
    }

    private static JPanel buildGreenTeamPanel() {
        JPanel greenPanel = new JPanel(new BorderLayout());
        greenPanel.setBorder(new LineBorder(Color.GRAY, 2));
        greenPanel.setBackground(Color.GREEN);
        greenPanel.setName("green-panel");

        JLabel greenLabel = new JLabel("GREEN TEAM", SwingConstants.CENTER);
        greenLabel.setFont(new Font("Arial", Font.BOLD, 25));
        greenLabel.setForeground(Color.GREEN);

        JPanel greenLabelPanel = new JPanel();
        greenLabelPanel.setBackground(Color.BLACK);
        greenLabelPanel.add(greenLabel);
        greenPanel.add(greenLabelPanel, BorderLayout.NORTH);

        JPanel greenListPanel = new JPanel(new GridLayout(15, 1));
        buildGreenTeamListPanel(greenListPanel);

        greenPanel.add(greenListPanel, BorderLayout.CENTER);

        return greenPanel;
    }

    private static JPanel buildRedTeamPanel() {
        JPanel redPanel = new JPanel(new BorderLayout());
        redPanel.setBorder(new LineBorder(Color.GRAY, 2));
        redPanel.setBackground(Color.PINK);
        redPanel.setName("red-panel");

        JLabel redLabel = new JLabel("RED TEAM", SwingConstants.CENTER);
        redLabel.setFont(new Font("Arial", Font.BOLD, 25));
        redLabel.setForeground(Color.RED);

        JPanel redLabelPanel = new JPanel();
        redLabelPanel.setBackground(Color.BLACK);
        redLabelPanel.add(redLabel);
        redPanel.add(redLabelPanel, BorderLayout.NORTH);

        JPanel redListPanel = new JPanel(new GridLayout(15, 1));
        buildRedTeamListPanel(redListPanel);

        redPanel.add(redListPanel, BorderLayout.CENTER);

        return redPanel;
    }

    private static void buildGreenTeamListPanel(JPanel greenListPanel) {
        greenListPanel.removeAll();

        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if (!playerText.isEmpty()) {
                JLabel playerLabel = new JLabel(playerText, SwingConstants.CENTER);
                playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                greenListPanel.add(playerLabel);
            }
        }
    }

    private static void buildRedTeamListPanel(JPanel redListPanel) {
        redListPanel.removeAll();

        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][1]);
            if (!playerText.isEmpty()) {
                JLabel playerLabel = new JLabel(playerText, SwingConstants.CENTER);
                playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                redListPanel.add(playerLabel);
            }
        }
    }

    private static JPanel buildTimerPanel() {
        timerLabel = new JLabel("Time Left: 06:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setForeground(Color.WHITE);

        JPanel timerPanel = new JPanel();
        timerPanel.setBackground(Color.BLACK);
        timerPanel.add(timerLabel);

        return timerPanel;
    }

    private static String getPlayerText(JTextField field) {
        if(field == null) return "";

        String name = field.getText().trim();
        return name.isEmpty() ? "" : name + " - 0 pts";
    }

    private static void startTimer() {
        Timer timer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
            } else {
                ((Timer) e.getSource()).stop();//need to code what happens when timer hits 0
            }
        });
        timer.start();
    }
}

