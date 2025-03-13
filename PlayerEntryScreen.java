import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

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

        // Key Bindings for F5 (Exit) and F12 (Clear)
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "exit");
        frame.getRootPane().getActionMap()
            .put("exit", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    System.exit(0);
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
                String codeName = JOptionPane.showInputDialog("New player! Enter your codename:");
                if (codeName != null && !codeName.trim().isEmpty()) {
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO player (id, codename) VALUES (?, ?)");
                    insertStmt.setInt(1, playerID);
                    insertStmt.setString(2, codeName);
                    insertStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Codename saved successfully!");
                    writeToScreen(codeName);
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

    private static void writeToScreen(String codeName) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                if (textFields[i][j].getText().isEmpty()) {
                    textFields[i][j].setText(codeName);
                    return;
                }
            }
        }
    }
}

