import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PlayerEntryScreen {
    private static JTextField[][] textFields = new JTextField[15][2];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlayerEntryScreen::showSplashScreen);
    }

    private static void showSplashScreen() {
        JFrame splashFrame = new JFrame("Splash Screen");
        splashFrame.setUndecorated(true);
        splashFrame.setSize(900, 500);
        splashFrame.setLocationRelativeTo(null);

        ImageIcon photon = new ImageIcon("photon.png");
        JLabel splashLabel = new JLabel(photon, SwingConstants.CENTER);
        splashFrame.add(splashLabel);

        splashFrame.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            splashFrame.dispose();
            createAndShowGUI();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Player Entry Screen");
        frame.setSize(400, 600);
        frame.setLayout(new GridLayout(17, 2));
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

        JButton submitButton = new JButton("Enter Player ID");
        JTextField playerIDField = new JTextField();

        frame.add(playerIDField);
        frame.add(submitButton);

        submitButton.addActionListener(e -> {
            String playerIDText = playerIDField.getText().trim();
            if (!playerIDText.matches("\\d+")) {
                JOptionPane.showMessageDialog(frame, "Invalid Player ID! Must be a number.");
                return;
            }
            int playerID = Integer.parseInt(playerIDText);
            processPlayerID(playerID);
        });

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "startCountdown");
        frame.getRootPane().getActionMap()
            .put("startCountdown", new AbstractAction() {
                private Timer countdownTimer;
                private int timeLeft = 30;

                @Override
                public void actionPerformed(ActionEvent e) {
                    countdownTimer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            if (timeLeft > 0) {
                                System.out.println("Starting game action display in " + timeLeft + " seconds...");
                                timeLeft--;
                            } else {
                                countdownTimer.stop();
                                frame.dispose(); // Close player entry screen
                                new GameActionDisplay(textFields); // Show game action display
                            }
                        }
                    });

                    countdownTimer.start();
                    JOptionPane.showMessageDialog(frame, "Game action display will start in 30 seconds!", 
                                                  "Countdown Timer", JOptionPane.INFORMATION_MESSAGE);
                }
            });

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "clear");
        frame.getRootPane().getActionMap()
            .put("clear", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < 15; i++) {
                        for (int j = 0; j < 2; j++) {
                            textFields[i][j].setText("");
                        }
                    }
                }
            });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void processPlayerID(int playerID) {
        try (Connection conn = connectToDatabase()) {
            if (conn == null) return;

            PreparedStatement pstmt = conn.prepareStatement("SELECT codename FROM player WHERE id = ?");
            pstmt.setInt(1, playerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String codename = rs.getString("codename");
                JOptionPane.showMessageDialog(null, "Welcome back " + codename);
                writeToScreen(codename);
            } else {
                String newCodename = JOptionPane.showInputDialog("New player! Enter your codename:");
                if (newCodename != null && !newCodename.trim().isEmpty()) {
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO player (id, codename) VALUES (?, ?)");
                    insertStmt.setInt(1, playerID);
                    insertStmt.setString(2, newCodename);
                    insertStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Codename saved successfully!");
                    writeToScreen(newCodename);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
    }

    private static Connection connectToDatabase() {
        String url = "jdbc:postgresql://localhost:5432/photon";
        String user = "student";
        String password = "student";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage());
            return null;
        }
    }

    private static void writeToScreen(String newCodename) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                if (textFields[i][j].getText().isEmpty()) {
                    textFields[i][j].setText(newCodename);
                    return;
                }
            }
        }
    }
}

class GameActionDisplay {
    private JTextField[][] textFields;

    public GameActionDisplay(JTextField[][] textFields) {
        this.textFields = textFields;
        createGameScreen();
    }

    private void createGameScreen() {
        JFrame gameFrame = new JFrame("Game Action Display");
        gameFrame.setSize(600, 400);
        gameFrame.setLayout(new GridLayout(2, 1));

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
        
        gameFrame.add(topPanel);
        gameFrame.add(bottomPanel);
        gameFrame.setVisible(true);
    }

    private String getPlayerText(JTextField field) {
        String name = field.getText().trim();
        return name.isEmpty() ? "" : name + " - 0 pts";
    }
}
