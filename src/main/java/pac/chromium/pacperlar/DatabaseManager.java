package pac.chromium.pacperlar;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
    private final burger plugin;
    private Connection connection;

    public DatabaseManager(burger plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/hearts.db");
            createTable();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to SQLite database!", e);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing SQLite connection!", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_hearts (" +
                     "uuid TEXT PRIMARY KEY," +
                     "hearts INTEGER NOT NULL" +
                     ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error creating table!", e);
        }
    }

    public int getHearts(UUID uuid) {
        String sql = "SELECT hearts FROM player_hearts WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hearts");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error getting hearts for " + uuid, e);
        }
        return -1; // Not found
    }

    public void setHearts(UUID uuid, int hearts) {
        String sql = "INSERT OR REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, hearts);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error setting hearts for " + uuid, e);
        }
    }

    public void clearDatabase() {
        String sql = "DELETE FROM player_hearts";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error clearing database!", e);
        }
    }
}
