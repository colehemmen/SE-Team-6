import java.sql.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.util.Scanner;

public class PlayerEntryScreen {
    private static boolean running = true;
    public static void main(String[] args) {
        // GUI Component to capture key events
        JFrame frame = new JFrame("Press F5 to Exit");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add KeyListener to detect F5 press
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    running = false; // Stop the loop when F5 is pressed
                    System.out.println("F5 pressed. Exiting loop...");
                    frame.dispose(); // Close GUI
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });

        // Define connection parameters
        String url = "jdbc:postgresql://localhost:5432/photon";
        String user = "student";
        String password = "student"; // Uncomment if password is required

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Scanner scan = new Scanner(System.in);

        try {
            // Connect to PostgreSQL
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");

            // Execute a query to get PostgreSQL version
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT version();");
            if (rs.next()) {
                System.out.println("Connected to - " + rs.getString(1));
            }

            // Create player table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS player (id INT, codename VARCHAR(30));";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'player' ensured to exist.");

            while(running) {
                System.out.println("Please enter your player ID:");
                int playerID = scan.nextInt();
                String remvNL = scan.nextLine();

                //Check for existing codename at playerID, and if none, prompt for user to enter codename
                String checkSQL = "SELECT codename FROM players WHERE id = ?;";
                pstmt = conn.prepareStatement(checkSQL);
                pstmt.setInt(1, playerID);
                rs = pstmt.executeQuery();
            
                if (rs.next()) {
                    System.out.println("Player found: Welcome back " + rs.getString("codename"));
                } else {
                    System.out.println("Player ID not found. Welcome first time player! Please enter a codename:");
                    String codeName = scan.nextLine();
                    // Inserting data into 'players' table
                    String insertSQL = "INSERT INTO players (id, codename) VALUES (?, ?);";
                    pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setInt(1, playerID);
                    pstmt.setString(2, codeName);
                    pstmt.executeUpdate();
                    System.out.println("Data inserted successfully.");
                }
                System.out.println("Please enter your equipment ID:");
                int equipmentID = scan.nextInt();
            }
            
            scan.close();

            // Fetch and display data from the table
            String selectSQL = "SELECT * FROM players;";
            pstmt = conn.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Codename: " + rs.getString("codename"));
            }

        } catch (SQLException e) {
            System.out.println("Error connecting to PostgreSQL database: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
}

