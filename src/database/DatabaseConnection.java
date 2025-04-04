package database;

import javax.swing.*;
import java.sql.*;

public class DatabaseConnection {

    private static final String url = "jdbc:postgresql://localhost:5432/photon";
    private static final String user = "student";
    private static final String password = "student";

    private static Connection connection;

    public DatabaseConnection() {
        connectToDatabase();
    }

    public String getCodenameByPlayerId(Integer playerId) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT codename FROM player WHERE id = ?");
            query.setInt(1, playerId);

            ResultSet rs = query.executeQuery();

            if (rs.next()) {
                return rs.getString("codename");
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public void createNewPlayerByIdAndCodename(Integer playerId, String codeName) {
        try {
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO player (id, codename) VALUES (?, ?)");
            insertStmt.setInt(1, playerId);
            insertStmt.setString(2, codeName);
            insertStmt.executeUpdate();
        } catch (SQLException ignored) { }
    }

    private static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage());
        }
    }

}
