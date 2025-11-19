import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideAssistantDAO {

    public boolean addAssistant(int rideId, int riderId, String assistantName) {
        String sql = "INSERT INTO Ride_Assistants (ride_id, rider_id, assistant_name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, riderId);
            stmt.setString(3, assistantName);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding assistant: " + e.getMessage());
        }
        return false;
    }

    public List<String> getAssistantsByRide(int rideId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT assistant_name FROM Ride_Assistants WHERE ride_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(rs.getString("assistant_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error reading assistants: " + e.getMessage());
        }
        return list;
    }
}
