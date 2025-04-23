package screens;

import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import database.DatabaseConnection;
import udp.UDPClient;
import classes.Player;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class GameAction {
    private static DatabaseConnection databaseConnection;
    private static JTextField[][] textFields;
    private static UDPClient udpClient;

    private static javax.swing.Timer timer;
    private static javazoom.jl.player.Player playMP3;

    private static JPanel mainPanel;
    private static JLabel timerLabel;
    private static int timeRemaining = 360;

    private static final Set<String> baseTaggers = new HashSet<>();

    private static final Map<String, Integer> playerScores = new HashMap<>();

    private static JPanel greenListPanel;
    private static JPanel redListPanel;

    private static JTextArea eventFeedArea;

    private static JLabel greenTeamScoreLabel;
    private static JLabel redTeamScoreLabel;

    private static JLabel greenLabel;
    private static JLabel redLabel;

    private static JPanel greenPanel;
    private static JPanel redPanel;

    private static javax.swing.Timer flashTimer;

    private static String previousFlashingTeam = "";
    private static String flashingTeam = "";

    public GameAction(JTextField[][] tfs, DatabaseConnection db, UDPClient udp, CardLayout cLayout, JPanel cardPanel) {
        databaseConnection = db;
        textFields = tfs;
        udpClient = udp;

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setName("root");

        JPanel timerPanel = buildTimerPanel(cLayout, cardPanel);
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

    public static void run(javazoom.jl.player.Player mp3Player) {
        playMP3 = mp3Player;

        startTimer();
        startFlashingEffect();
    }

    public static void updateTextValues(JTextField[][] tfs) {
        textFields = tfs;

        buildGreenTeamListPanel(greenListPanel, "");
        buildRedTeamListPanel(redListPanel, "");
        updateTeamScores();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void processEvent(String event) {
        try {
            if (event == null || event.isEmpty()) return;

            String[] parts = event.split(":");
            if (parts.length < 2) return;

            String attackerId = parts[0];
            String targetId = parts[1];

            String attackerCodename = databaseConnection.getCodenameByPlayerId(Integer.parseInt(attackerId));
            String targetCodename = databaseConnection.getCodenameByPlayerId(Integer.parseInt(targetId));

            boolean isGreenAttacker = isGreenTeam(attackerCodename);
            boolean isGreenTarget = targetCodename != null && isGreenTeam(targetCodename);

            if (targetId.equals("43")) { // Green base hit
                if (!isGreenAttacker) { // Red player tags the base (green player cannot tag their own base)
                    playerScores.put(attackerCodename, playerScores.getOrDefault(attackerId, 0) + 100);
                    baseTaggers.add(attackerCodename);
                    addEventToFeed("Player " + attackerCodename + " hit the GREEN BASE!");
                }
            } else if (targetId.equals("53")) { // Red base hit
                if (isGreenAttacker) { // Green player tags the base (red player cannot tag their own base)
                    playerScores.put(attackerCodename, playerScores.getOrDefault(attackerId, 0) + 100);
                    baseTaggers.add(attackerCodename);
                    addEventToFeed("Player " + attackerCodename + " hit the RED BASE!");
                }
            } else {
                if (!isGreenAttacker && !isGreenTarget) return; // Friendly fire disabled

                playerScores.put(attackerCodename, playerScores.getOrDefault(attackerCodename, 0) + 10);
                playerScores.put(targetCodename, Math.max(0, playerScores.getOrDefault(targetCodename, 0) - 10));

                addEventToFeed("Player " + attackerCodename + " hit Player " + targetCodename + "!");
            }

            if (isGreenAttacker) {
                buildGreenTeamListPanel(greenListPanel, "");
                buildRedTeamListPanel(redListPanel, attackerCodename);
            } else {
                buildGreenTeamListPanel(greenListPanel, attackerCodename);
                buildRedTeamListPanel(redListPanel, "");
            }

            updateTeamScores();

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static JPanel getMainPanel() {
        return mainPanel;
    }

    private static boolean isGreenTeam(String playerCodename) {
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if(playerText.isEmpty()) break;

            if (playerText.contains(playerCodename)) return true;
        }
        return false;
    }

    private static JPanel buildGreenTeamPanel() {
        greenPanel = new JPanel(new BorderLayout());
        greenPanel.setBorder(new LineBorder(Color.GRAY, 2));
        greenPanel.setBackground(Color.GREEN);
        greenPanel.setName("green-panel");

        greenLabel = new JLabel("GREEN TEAM", SwingConstants.CENTER);
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
        buildGreenTeamListPanel(greenListPanel, "");
        greenPanel.add(greenListPanel, BorderLayout.CENTER);

        return greenPanel;
    }

    private static JPanel buildRedTeamPanel() {
        redPanel = new JPanel(new BorderLayout());
        redPanel.setBorder(new LineBorder(Color.GRAY, 2));
        redPanel.setBackground(Color.PINK);
        redPanel.setName("red-panel");

        redLabel = new JLabel("RED TEAM", SwingConstants.CENTER);
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
        buildRedTeamListPanel(redListPanel, "");
        redPanel.add(redListPanel, BorderLayout.CENTER);

        return redPanel;
    }

    private static void buildGreenTeamListPanel(JPanel panel, String attackerCodeName) {
        panel.removeAll();

        List<Player> greenPlayers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String playerText = getPlayerText(textFields[i][0]);
            if (!playerText.isEmpty()) {
                String playerCodename = playerText.split(" ")[0];
                int score = playerScores.getOrDefault(playerCodename, 0);
                greenPlayers.add(new Player(playerCodename, score));
            }
        }

        greenPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore())); // Sort by score descending

        for (Player player : greenPlayers) {
            JLabel label;
            if(attackerCodeName.equals(player.getCodename()) || baseTaggers.contains(player.getCodename())) {
                label = new JLabel("🄱  " + player.getCodename() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            }
            else {
                label = new JLabel(player.getCodename() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            }
            panel.add(label);
        }
    }

    private static void buildRedTeamListPanel(JPanel panel, String attackerCodeName) {
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
            JLabel label;
            if(attackerCodeName.equals(player.getCodename()) || baseTaggers.contains(player.getCodename())) {
                label = new JLabel("🄱  " + player.getCodename() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            }
            else {
                label = new JLabel(player.getCodename() + " - " + player.getScore() + " pts", SwingConstants.CENTER);
            }
            panel.add(label);
        }
    }

    private static JPanel buildTimerPanel(CardLayout cLayout, JPanel cPanel) {
        timerLabel = new JLabel("Time Left: 06:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setForeground(Color.WHITE);

        JButton returnButton = new JButton("Return to Player Entry");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 16));
        returnButton.addActionListener(e -> {
            timer.stop();
            timeRemaining = 360; // reset countdown clock
            flashTimer.stop(); // stop timer
            playMP3.close(); // stops music

            udpClient.transitStatusCode(221); // transmit game stopped if returned to player action screen

            cLayout.show(cPanel, "player-entry");
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
        Action timerTimer = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    int minutes = timeRemaining / 60;
                    int seconds = timeRemaining % 60;
                    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    if (flashTimer != null) flashTimer.stop();

                    udpClient.transitStatusCode(221);
                }
            }
        };

        timer = new Timer(1000, timerTimer);
        timer.start();

        timerTimer.actionPerformed(null);
    }

    private static void updateTeamScores() {
        int greenScore = 0;
        int redScore = 0;

        for (int i = 0; i < 15; i++) {
            String greenText = getPlayerText(textFields[i][0]);
            String redText = getPlayerText(textFields[i][1]);

            if (!greenText.isEmpty()) {
                String codeName = greenText.split(" ")[0];
                greenScore += playerScores.getOrDefault(codeName, 0);
            }

            if (!redText.isEmpty()) {
                String codeName = redText.split(" ")[0];
                redScore += playerScores.getOrDefault(codeName, 0);
            }
        }

        if (greenTeamScoreLabel != null) greenTeamScoreLabel.setText("Team Score: " + greenScore);
        if (redTeamScoreLabel != null) redTeamScoreLabel.setText("Team Score: " + redScore);

        if(greenScore > redScore) {
            flashingTeam = "green";
        } else if (redScore > greenScore) {
            flashingTeam = "red";
        } else {
            flashingTeam = "";
        }
    }

    private static void startFlashingEffect() {
        flashTimer = new javax.swing.Timer(500, e -> {
            if (flashingTeam.equals("green")) {
                if (previousFlashingTeam.equals("green")) {
                    // Continue flashing green team with yellow
                    if (greenLabel.getForeground().equals(Color.GREEN)) {
                        // Change to yellow
                        greenLabel.setForeground(Color.YELLOW);
                    } else {
                        // Change back to green
                        greenLabel.setForeground(Color.GREEN);
                    }
                } else {
                    // Team has changed to green, reset red team to its normal color
                    redLabel.setForeground(Color.RED);  // Set red team's normal color
                    greenLabel.setForeground(Color.GREEN);  // Set green team's normal color
                }
            } else if (flashingTeam.equals("red")) {
                if (previousFlashingTeam.equals("red")) {
                    // Continue flashing red team with yellow
                    if (redLabel.getForeground().equals(Color.RED)) {
                        // Change to yellow
                        redLabel.setForeground(Color.YELLOW);
                    } else {
                        // Change back to red
                        redLabel.setForeground(Color.RED);
                    }
                } else {
                    // Team has changed to red, reset green team to its normal color
                    greenLabel.setForeground(Color.GREEN);  // Set green team's normal color
                    redLabel.setForeground(Color.RED);  // Set red team's normal color
                }
            }

            // Update the previous flashing team
            previousFlashingTeam = flashingTeam;

            // Repaint and validate to apply changes
            greenPanel.repaint();
            redPanel.repaint();
            greenPanel.validate();
            redPanel.validate();
        });

        flashTimer.start();
    }
}
