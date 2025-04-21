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

    public Boolean createTables() throws SQLException {
        PreparedStatement query = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players ("
                + "id INT PRIMARY KEY, "
                + "codename VARCHAR(30) UNIQUE NOT NULL)");

        int result = query.executeUpdate();

        return result == 0;
    }

    public String getCodenameByPlayerId(Integer playerId) throws SQLException {
        PreparedStatement query = connection.prepareStatement("SELECT codename FROM players WHERE id = ?");
        query.setInt(1, playerId);

        ResultSet rs = query.executeQuery();

        if (rs.next()) {
            return rs.getString("codename");
        } else {
            return null;
        }
    }

    public void createNewPlayerByIdAndCodename(Integer playerId, String codeName) throws SQLException {
        PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO players (id, codename) VALUES (?, ?)");
        insertStmt.setInt(1, playerId);
        insertStmt.setString(2, codeName);
        insertStmt.executeUpdate();
    }

    private static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage());
        }
    }

}
