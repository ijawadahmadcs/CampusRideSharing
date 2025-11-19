import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public int createFeedback(int rideId, int rating, String comments) {
        String sql = "INSERT INTO Feedback (ride_id, rating, comments) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, rideId);
            stmt.setInt(2, rating);
            stmt.setString(3, comments);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating feedback: " + e.getMessage());
        }
        return -1;
    }

    public List<Feedback> getFeedbacksByRide(int rideId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE ride_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback(
                        rs.getInt("feedback_id"),
                        rs.getInt("ride_id"),
                        rs.getInt("rating"),
                        rs.getString("comments")
                    );
                    list.add(fb);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading feedbacks: " + e.getMessage());
        }
        return list;
    }
}
