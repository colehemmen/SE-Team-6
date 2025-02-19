import java.sql.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.util.Scanner;

public class PlayerEntryScreen {
    private static boolean running = true;
    private static Connection conn;

    public static void main(String[] args) {
        // Define connection parameters
        String url = "jdbc:postgresql://localhost:5432/photon";
        String user = "student";
        String password = "student";

        try {
            // Connect to PostgreSQL
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");

            // Execute a query to get PostgreSQL version
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version();");
            if (rs.next()) {
                System.out.println("Connected to - " + rs.getString(1));
            }

            // Create player table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS player (id INT PRIMARY KEY, codename VARCHAR(30));";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'player' ensured to exist.");
            rs.close();
            stmt.close();
        } catch (SQLException a) {
            System.out.println("Error connecting to PostgreSQL database: " + a.getMessage());
            return;
        }

        Scanner scan = new Scanner(System.in);

        // GUI Component to capture key events
        JFrame frame = new JFrame("Press F5 to Exit");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        addKeyListenerToFrame(frame);

        try {
            while (running) {
                System.out.println("Please enter your player ID:");
                int playerID = scan.nextInt();
                scan.nextLine(); // Consume newline

                // Check for existing codename
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT codename FROM player WHERE id = ?")) {
                    pstmt.setInt(1, playerID);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("Player found: Welcome back " + rs.getString("codename"));
                    } else {
                        System.out.println("Player ID not found. Welcome first-time player! Please enter a codename:");
                        String codeName = scan.nextLine();

                        try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO player (id, codename) VALUES (?, ?)")) {
                            insertStmt.setInt(1, playerID);
                            insertStmt.setString(2, codeName);
                            insertStmt.executeUpdate();
                            System.out.println("Data inserted successfully.");
                        }
                    }
                    rs.close();
                }

                System.out.println("Please enter your equipment ID:");
                int equipmentID = scan.nextInt();
                scan.nextLine(); // Consume newline
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing database connection: " + ex.getMessage());
            }
            scan.close();
        }
    }

    private static void addKeyListenerToFrame(JFrame frame) {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    running = false;
                    System.out.println("F5 pressed. Exiting loop...");
                    frame.dispose();

                    // Fetch and display data from the table
                    try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM player");
                         ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            System.out.println("ID: " + rs.getInt("id") + ", Codename: " + rs.getString("codename"));
                        }
                    } catch (SQLException j) {
                        System.out.println("Error fetching data from PostgreSQL database: " + j.getMessage());
                    }
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }
}
