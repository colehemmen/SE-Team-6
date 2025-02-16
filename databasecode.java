import java.sql.*;
import java.util.Scanner;

public class PostgresTableExample {
    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/your_database";
    private static final String USER = "Student";
    private static final String PASSWORD = "Student";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to PostgreSQL successfully!");

            // Step 1: Create Table if one does not exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS players ("
                                  + "id INT PRIMARY KEY, "
                                  + "codename VARCHAR(30) UNIQUE NOT NULL)";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSQL);
                System.out.println("Table 'players' is ready.");
            }

            //Get User Input
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); //new line

            System.out.print("Enter player codename: ");
            String codename = scanner.nextLine();

            //Check if codename already exists in databse
            String checkSQL = "SELECT id FROM players WHERE codename = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, codename);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Error: Codename '" + codename + "' already exists for ID: " + rs.getInt("id"));
                    return; // Exits instead of inserting - notsure how the UDP will assign ID#'s
                }
            }

            //Insert the record since codename does not in database
            String insertSQL = "INSERT INTO players (id, codename) VALUES (?, ?) ON CONFLICT (id) DO NOTHING";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setInt(1, playerId);
                pstmt.setString(2, codename);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("New player added successfully!");
                } else {
                    System.out.println("Player ID already exists, skipping insertion.");
                }
            }

            //Retrieve and print all records
            String selectSQL = "SELECT * FROM players";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {

                System.out.println("\nCurrent players in the database:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Codename: " + rs.getString("codename"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}