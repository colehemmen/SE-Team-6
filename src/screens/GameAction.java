package screens;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import database.DatabaseConnection;

import java.util.*;
import java.util.List;

public class GameAction {
    private static DatabaseConnection databaseConnection;
    private static JTextField[][] textFields;


    private static JPanel mainPanel;
    private static JLabel timerLabel;
    private static int timeRemaining = 360;


    private static final Map<String, Integer> playerScores = new HashMap<>();

    private static JPanel greenListPanel;
    private static JPanel redListPanel;

    private static JTextArea eventFeedArea;
    private static JLabel greenTeamScoreLabel;
    private static JLabel redTeamScoreLabel;
    private static JPanel greenPanel;
    private static JPanel redPanel;

    private static javax.swing.Timer flashTimer;
    private static boolean flashToggle = false;

    public GameAction(JTextField[][] tfs, DatabaseConnection db) {
        databaseConnection = db;
        textFields = tfs;

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setName("root");

        JPanel timerPanel = buildTimerPanel();
        rootPanel.add(timerPanel, BorderLayout.NORTH);

        greenPanel = buildGreenTeamPanel();
        redPanel = buildRedTeamPanel();
        JPanel eventFeedPanel = buildEventFeedPanel();

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(greenPanel);
        centerPanel.add(eventFeedPanel);
        centerPanel.add(redPanel);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel = rootPanel;
    }

    public static void run() {
        startTimer();
        startFlashingEffect();
    }

    public static void updateTextValues(JTextField[][] tfs) {
        textFields = tfs;

        buildGreenTeamListPanel(greenListPanel);
        buildRedTeamListPanel(redListPanel);
        updateTeamScores();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void processEvent(String event) {
        if (event == null || event.isEmpty()) return;

        String[] parts = event.split(":");
        if (parts.length < 2) return;

        String attackerId = parts[0];
        String targetId = parts[1];
        String attackerCodename = databaseConnection.getCodenameByPlayerId(Integer.parseInt(attackerId));

        boolean isGreenAttacker = isGreenTeam(attackerId);
        boolean isGreenTarget = isGreenTeam(targetId);

        if (targetId.equals("43")) {
            if (!isGreenAttacker) {
                playerScores.put(attackerId, playerScores.getOrDefault(attackerId, 0) + 100);
                addEventToFeed("Player " + attackerId + " hit the GREEN BASE!");
                for (Component comp : redListPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        String labelText = label.getText();
                
                        if (labelText.contains(attackerCodename)) {
                            // Match found
                            if(!labelText.startsWith("🄱")) {
                                label.setText("🄱  " + labelText);
                            }
                            System.out.println("Found attacker codename in red list: " + labelText);
                            break; // optional: exit loop once found
                        }
                    }
                }
            }
        } else if (targetId.equals("53")) {
            if (isGreenAttacker) {
                playerScores.put(attackerId, playerScores.getOrDefault(attackerId, 0) + 100);
                addEventToFeed("Player " + attackerId + " hit the RED BASE!");
                for (Component comp : greenListPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        String labelText = label.getText();
                
                        if (labelText.contains(attackerCodename)) {
                            // Match found
                            if(!labelText.startsWith("🄱")) {
                                label.setText("🄱  " + labelText);
                            }
                            System.out.println("Found attacker codename in red list: " + labelText);
                            break; // optional: exit loop once found
                        }
                    }
                }
            }
        } else {
            if (isGreenAttacker == isGreenTarget) return;

            playerScores.put(attackerId, playerScores.getOrDefault(attackerId, 0) + 10);
            playerScores.put(targetId, Math.max(0, playerScores.getOrDefault(targetId, 0) - 10));

            addEventToFeed("Player " + attackerId + " hit Player " + targetId + "!");
        }

        buildGreenTeamListPanel(greenListPanel);
        buildRedTeamListPanel(redListPanel);
        updateTeamScores();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static JPanel getMainPanel() {
        return mainPanel;
    }

    private static boolean isGreenTeam(String playerId) {
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if (playerText.startsWith(playerId)) return true;
        }
        return false;
    }

    private static JPanel buildGreenTeamPanel() {
        greenPanel = new JPanel(new BorderLayout());
        greenPanel.setBorder(new LineBorder(Color.GRAY, 2));
        greenPanel.setBackground(Color.GREEN);
        greenPanel.setName("green-panel");

        JLabel greenLabel = new JLabel("GREEN TEAM", SwingConstants.CENTER);
        greenLabel.setFont(new Font("Arial", Font.BOLD, 25));
        greenLabel.setForeground(Color.GREEN);

        greenTeamScoreLabel = new JLabel("Team Score: 0", SwingConstants.CENTER);
        greenTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        greenTeamScoreLabel.setForeground(Color.WHITE);

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.BLACK);
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(greenLabel);
        labelPanel.add(greenTeamScoreLabel);

        greenPanel.add(labelPanel, BorderLayout.NORTH);

        greenListPanel = new JPanel(new GridLayout(15, 1));
        buildGreenTeamListPanel(greenListPanel);
        greenPanel.add(greenListPanel, BorderLayout.CENTER);

        return greenPanel;
    }

    private static JPanel buildRedTeamPanel() {
        redPanel = new JPanel(new BorderLayout());
        redPanel.setBorder(new LineBorder(Color.GRAY, 2));
        redPanel.setBackground(Color.PINK);
        redPanel.setName("red-panel");

        JLabel redLabel = new JLabel("RED TEAM", SwingConstants.CENTER);
        redLabel.setFont(new Font("Arial", Font.BOLD, 25));
        redLabel.setForeground(Color.RED);

        redTeamScoreLabel = new JLabel("Team Score: 0", SwingConstants.CENTER);
        redTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        redTeamScoreLabel.setForeground(Color.WHITE);

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.BLACK);
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(redLabel);
        labelPanel.add(redTeamScoreLabel);

        redPanel.add(labelPanel, BorderLayout.NORTH);

        redListPanel = new JPanel(new GridLayout(15, 1));
        buildRedTeamListPanel(redListPanel);
        redPanel.add(redListPanel, BorderLayout.CENTER);

        return redPanel;
    }

    private static void buildGreenTeamListPanel(JPanel panel) {
        panel.removeAll();

        List<Player> greenPlayers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if (!playerText.isEmpty()) {
                String playerId = playerText.split(" ")[0];
                int score = playerScores.getOrDefault(playerId, 0);
                greenPlayers.add(new Player(playerId, score));
            }
        }

        greenPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore())); // Sort by score descending

        for (Player player : greenPlayers) {
            JLabel label = new JLabel(player.getId() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            panel.add(label);
        }
    }

    private static void buildRedTeamListPanel(JPanel panel) {
        panel.removeAll();

        List<Player> redPlayers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][1]);
            if (!playerText.isEmpty()) {
                String playerId = playerText.split(" ")[0];
                int score = playerScores.getOrDefault(playerId, 0);
                redPlayers.add(new Player(playerId, score));
            }
        }

        redPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore())); // Sort by score descending

        for (Player player : redPlayers) {
            JLabel label = new JLabel(player.getId() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            panel.add(label);
        }
    }

    private static JPanel buildTimerPanel() {
        timerLabel = new JLabel("Time Left: 06:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setForeground(Color.WHITE);

        JButton returnButton = new JButton("Return to Player Entry");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 16));
        returnButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Returning to Player Entry screen...");
        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.add(timerLabel);
        panel.add(returnButton);

        return panel;
    }

    private static JPanel buildEventFeedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.GRAY, 2));
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel label = new JLabel("Event Feed", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(Color.BLACK);

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.BLACK);
        labelPanel.add(label);
        panel.add(labelPanel, BorderLayout.NORTH);

        eventFeedArea = new JTextArea(10, 20);
        eventFeedArea.setEditable(false);
        eventFeedArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(eventFeedArea);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private static String getPlayerText(JTextField field) {
        if (field == null) return "";
        String name = field.getText().trim();
        return name.isEmpty() ? "" : name + " - 0 pts";
    }

    private static void addEventToFeed(String msg) {
        eventFeedArea.append(msg + "\n");
        eventFeedArea.setCaretPosition(eventFeedArea.getDocument().getLength());
    }

    private static void startTimer() {
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                if (flashTimer != null) flashTimer.stop();
            }
        });
        timer.start();
    }

    private static void updateTeamScores() {
        int greenScore = 0;
        int redScore = 0;

        for (int i = 0; i < 15; i++) {
            String greenText = getPlayerText(textFields[i][0]);
            String redText = getPlayerText(textFields[i][1]);

            if (!greenText.isEmpty()) {
                String id = greenText.split(" ")[0];
                greenScore += playerScores.getOrDefault(id, 0);
            }

            if (!redText.isEmpty()) {
                String id = redText.split(" ")[0];
                redScore += playerScores.getOrDefault(id, 0);
            }
        }

        if (greenTeamScoreLabel != null) greenTeamScoreLabel.setText("Team Score: " + greenScore);
        if (redTeamScoreLabel != null) redTeamScoreLabel.setText("Team Score: " + redScore);
    }

    private static void startFlashingEffect() {
        flashTimer = new javax.swing.Timer(500, e -> {
            flashToggle = !flashToggle;
            if (flashToggle) {
                greenPanel.setBackground(Color.DARK_GRAY);
                redPanel.setBackground(Color.PINK);
            } else {
                greenPanel.setBackground(Color.GREEN);
                redPanel.setBackground(Color.PINK);
            }
        });
        flashTimer.start();
    }

    private static class Player {
        private final String id;
        private final int score;

        public Player(String id, int score) {
            this.id = id;
            this.score = score;
        }

        public String getId() {
            return id;
        }

        public int getScore() {
            return score;
        }
    }
}
