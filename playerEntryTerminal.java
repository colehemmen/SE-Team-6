import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Scanner;

public class Player {

    private static Connection conn;

    public static void writeToScreen(TextField[][] textFields, String codeName, int equipmentID) {
        if(equipmentID % 2 == 1) {
            for(int i = 0; i < 15; i++) {
                for(int j = 0; j < 1; j++) {
                    if(textFields[i][j].getText().equals("")) {
                        textFields[i][j].setText(codeName);
                        return;
                    }
                }
            }
        }
        else {
            for(int i = 0; i < 15; i++) {
                for(int j = 1; j < 2; j++) {
                    if(textFields[i][j].getText().equals("")) {
                        textFields[i][j].setText(codeName);
                        return;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // Define connection parameters
        String url = "jdbc:postgresql://localhost:5432/photon";
        String user = "student";
        String password = "student";

        String codeName;

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

        Frame frame = new Frame("Player Screen");
        frame.setSize(400, 600);
        frame.setBackground(Color.BLACK);
        frame.setLayout(new GridLayout(17, 4)); //SET ROWS TO 17 IF NOT USING TERMINAL LABEL

        Label left = new Label("GREEN TEAM", Label.CENTER);
        left.setBackground(Color.GREEN);
        Label right = new Label("RED TEAM", Label.CENTER);
        right.setBackground(Color.PINK);

        frame.add(left);
        frame.add(right);
        
        // Creating a 2D array of TextAreas
        TextField[][] textFields = new TextField[15][2];
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                textFields[i][j] = new TextField();
                if(j == 0) {
                    textFields[i][j].setBackground(Color.GREEN);
                    textFields[i][j].setText("");
                    //counterL++;
                }
                else {
                    textFields[i][j].setBackground(Color.PINK);
                    textFields[i][j].setText("");
                    //counterR++;
                }
                frame.add(textFields[i][j]);
            }
        }

        Label f5 = new Label("[F5] = EXIT", Label.CENTER);
        f5.setBackground(Color.BLACK);
        f5.setForeground(Color.WHITE);
        f5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //System.out.println("Label clicked!");
                frame.requestFocus();
            }
        });
        Label f12 = new Label("[F12] = CLEAR", Label.CENTER);
        f12.setBackground(Color.BLACK);
        f12.setForeground(Color.WHITE);
        f12.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //System.out.println("Label clicked!");
                frame.requestFocus();
            }
        });

        frame.add(f5);
        frame.add(f12);

        KeyListener f5Pressed = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    frame.dispose();
                    System.exit(0); //JUST EXITING FOR RIGHT NOW BUT WHEN WE HAVE A GAME ACTION SCREEN TO SWITCH TO, 
                    //THAT WILL BE SWITCHED HERE
                }
            }

                @Override
                public void keyReleased(KeyEvent e) {}

                @Override
                public void keyTyped(KeyEvent e) {}
        };

        KeyListener f12Pressed = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F12) {
                    for (int i = 0; i < 15; i++) {
                        for (int j = 0; j < 2; j++) {
                            textFields[i][j].setText("");
                        }
                    }
                }
            }

                @Override
                public void keyReleased(KeyEvent e) {}

                @Override
                public void keyTyped(KeyEvent e) {}
        };

        frame.addKeyListener(f5Pressed);
        frame.addKeyListener(f12Pressed);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                frame.dispose();
            }
        });

        //Label terminal = new Label("TERMINAL", Label.LEFT) //Probably won't need this
        
        frame.setVisible(true);
        frame.requestFocus();

        try {
            for (int i = 0; i < 30; i++) {
                System.out.println("Please enter your player ID:");
                int playerID = scan.nextInt();
                scan.nextLine(); // Consume newline

                // Check for existing codename
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT codename FROM player WHERE id = ?")) {
                    pstmt.setInt(1, playerID);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("Player found: Welcome back " + rs.getString("codename"));
                        codeName = rs.getString("codename");
                        //writeToScreen(textFields, rs.getString("codename"));
                    } else {
                        System.out.println("Player ID not found. Welcome first-time player! Please enter a codename:");
                        codeName = scan.nextLine();
                        //writeToScreen(textFields, codeName);

                        try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO player (id, codename) VALUES (?, ?)")) {
                            insertStmt.setInt(1, playerID);
                            insertStmt.setString(2, codeName);
                            insertStmt.executeUpdate();
                            System.out.println("Data inserted successfully.");
                        }
                    }
                    rs.close();
                }

                System.out.println("Please enter your equipment ID: (#1 - 30)"); //odd = red team, even = green team
                int equipmentID = scan.nextInt();
                while((equipmentID < 1) || (equipmentID > 30)) {
                    System.out.println("ERROR   :   Please enter a valid equipment ID (#1 - 30)");
                    equipmentID = scan.nextInt();
                }
                writeToScreen(textFields, codeName, equipmentID);
                //THIS IS WHERE A FUNCTION WILL BE CALLED TO BROADCAST EQUIPMENT ID
                scan.nextLine(); // Consume newline
            }

            // Fetch and display data from the database (player table)
            /*try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM player");
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Codename: " + rs.getString("codename"));
            }
            } catch (SQLException j) {
                System.out.println("Error fetching data from PostgreSQL database: " + j.getMessage());
            }*/

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
}
