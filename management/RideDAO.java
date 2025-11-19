// RideDAO.java - Handles database operations for Ride class
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    // Create a new ride in DB and return generated ride ID
    public int createRide(int riderId, int driverId, int routeId, double fare) {
        String sql = "INSERT INTO Rides (rider_id, driver_id, route_id, fare, status) " +
                     "VALUES (?, ?, ?, ?, 'Pending')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, riderId);
            stmt.setInt(2, driverId);
            stmt.setInt(3, routeId);
            stmt.setDouble(4, fare);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int rideId = rs.getInt(1);
                        System.out.println("Ride created with ID: " + rideId);
                        return rideId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating ride: " + e.getMessage());
        }
        return -1; // Failed
    }

    // Update ride status (Pending, Ongoing, Completed, Cancelled)
    public boolean updateRideStatus(int rideId, String status) {
        String sql = "UPDATE Rides SET status = ? WHERE ride_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, rideId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating ride status: " + e.getMessage());
        }
        return false;
    }

    // Get all rides for a specific rider
    public List<String> getRidesByRider(int riderId) {
        List<String> rides = new ArrayList<>();
        String sql = "SELECT r.ride_id, r.fare, r.status, r.ride_time, " +
                     "ro.start_location, ro.end_location, u.name as driver_name " +
                     "FROM Rides r " +
                     "JOIN Routes ro ON r.route_id = ro.route_id " +
                     "JOIN Users u ON r.driver_id = u.user_id " +
                     "WHERE r.rider_id = ? ORDER BY r.ride_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, riderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ride = String.format(
                        "Ride#%d | %s→%s | Driver: %s | Fare: PKR %.2f | Status: %s | Time: %s",
                        rs.getInt("ride_id"),
                        rs.getString("start_location"),
                        rs.getString("end_location"),
                        rs.getString("driver_name"),
                        rs.getDouble("fare"),
                        rs.getString("status"),
                        rs.getTimestamp("ride_time")
                    );
                    rides.add(ride);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting rides: " + e.getMessage());
        }
        return rides;
    }

    // Get all rides for a specific driver
    public List<String> getRidesByDriver(int driverId) {
        List<String> rides = new ArrayList<>();
        String sql = "SELECT r.ride_id, r.fare, r.status, r.ride_time, " +
                     "ro.start_location, ro.end_location, u.name as rider_name " +
                     "FROM Rides r " +
                     "JOIN Routes ro ON r.route_id = ro.route_id " +
                     "JOIN Users u ON r.rider_id = u.user_id " +
                     "WHERE r.driver_id = ? ORDER BY r.ride_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ride = String.format(
                        "Ride#%d | %s→%s | Rider: %s | Fare: PKR %.2f | Status: %s | Time: %s",
                        rs.getInt("ride_id"),
                        rs.getString("start_location"),
                        rs.getString("end_location"),
                        rs.getString("rider_name"),
                        rs.getDouble("fare"),
                        rs.getString("status"),
                        rs.getTimestamp("ride_time")
                    );
                    rides.add(ride);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting rides: " + e.getMessage());
        }
        return rides;
    }
}
