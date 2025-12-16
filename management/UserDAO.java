// UserDAO.java - Data Access Object (DAO) for Users
// Handles all database operations related to Drivers and Riders

import java.sql.*;

public class UserDAO {

    // REGISTER NEW DRIVER
    public boolean registerDriver(String name, String email, String password, String licenseNumber) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            // Insert basic info into Users table
            String userSql = "INSERT INTO Users (name, email, password, user_type) VALUES (?, ?, ?, 'Driver')";
            PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, name);
            userStmt.setString(2, email);
            userStmt.setString(3, password);

            int rowsAffected = userStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = userStmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Insert driver-specific info into Drivers table
                    String driverSql = "INSERT INTO Drivers (driver_id, license_number) VALUES (?, ?)";
                    PreparedStatement driverStmt = conn.prepareStatement(driverSql);
                    driverStmt.setInt(1, userId);
                    driverStmt.setString(2, licenseNumber);
                    driverStmt.executeUpdate();

                    System.out.println("Driver registered successfully with ID: " + userId);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error registering driver: " + e.getMessage());
        }

        return false;
    }

    // REGISTER NEW RIDER
    public boolean registerRider(String name, String email, String password) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            // Insert basic info into Users table
            String userSql = "INSERT INTO Users (name, email, password, user_type) VALUES (?, ?, ?, 'Rider')";
            PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, name);
            userStmt.setString(2, email);
            userStmt.setString(3, password);

            int rowsAffected = userStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = userStmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Insert rider-specific info into Riders table
                    String riderSql = "INSERT INTO Riders (rider_id) VALUES (?)";
                    PreparedStatement riderStmt = conn.prepareStatement(riderSql);
                    riderStmt.setInt(1, userId);
                    riderStmt.executeUpdate();

                    System.out.println("Rider registered successfully with ID: " + userId);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error registering rider: " + e.getMessage());
        }

        return false;
    }

    // Get driver by ID (to refresh driver data from DB)
    public Driver getDriverById(int driverId) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            String sql = "SELECT u.user_id, u.name, u.email, u.password, d.license_number, d.total_earnings " +
                    "FROM Users u JOIN Drivers d ON u.user_id = d.driver_id " +
                    "WHERE u.user_id = ? AND u.user_type = 'Driver'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, driverId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("license_number"));
                driver.setTotalEarnings(rs.getDouble("total_earnings"));
                return driver;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching driver by ID: " + e.getMessage());
        }

        return null;
    }

    // DRIVER LOGIN
    public Driver loginDriver(String email, String password) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            String sql = "SELECT u.user_id, u.name, u.email, u.password, d.license_number, d.total_earnings " +
                    "FROM Users u JOIN Drivers d ON u.user_id = d.driver_id " +
                    "WHERE u.email = ? AND u.password = ? AND u.user_type = 'Driver'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("license_number"));
                driver.setTotalEarnings(rs.getDouble("total_earnings"));
                return driver;
            }

        } catch (SQLException e) {
            System.err.println("Error logging in driver: " + e.getMessage());
        }

        return null;
    }

    // RIDER LOGIN
    public Rider loginRider(String email, String password) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            String sql = "SELECT u.user_id, u.name, u.email, u.password, r.balance " +
                    "FROM Users u JOIN Riders r ON u.user_id = r.rider_id " +
                    "WHERE u.email = ? AND u.password = ? AND u.user_type = 'Rider'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Rider rider = new Rider(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"));
                rider.setBalance(rs.getDouble("balance"));
                return rider;
            }

        } catch (SQLException e) {
            System.err.println("Error logging in rider: " + e.getMessage());
        }

        return null;
    }

    // UPDATE RIDER BALANCE
    public boolean updateRiderBalance(int riderId, double balance) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            String sql = "UPDATE Riders SET balance = ? WHERE rider_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, balance);
            stmt.setInt(2, riderId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating rider balance: " + e.getMessage());
        }

        return false;
    }

    // UPDATE DRIVER EARNINGS
    public boolean updateDriverEarnings(int driverId, double totalEarnings) {
        Connection conn = DatabaseConfig.getConnection();

        try {
            String sql = "UPDATE Drivers SET total_earnings = ? WHERE driver_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, totalEarnings);
            stmt.setInt(2, driverId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating driver earnings: " + e.getMessage());
        }

        return false;
    }

    // Delete user and related data (rides, payments, feedback, vehicles, shifts,
    // driver/rider records)
    public boolean deleteUser(int userId) {
        Connection conn = DatabaseConfig.getNewConnection();
        if (conn == null)
            return false;
        try {
            conn.setAutoCommit(false);

            // Delete payments linked to rides of this user (as rider or driver)
            String delPayments = "DELETE p FROM Payments p JOIN Rides r ON p.ride_id = r.ride_id WHERE r.rider_id = ? OR r.driver_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delPayments)) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Delete ride assistants for rides of this user
            String delAssist = "DELETE ra FROM Ride_Assistants ra JOIN Rides r ON ra.ride_id = r.ride_id WHERE r.rider_id = ? OR r.driver_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delAssist)) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Delete feedbacks linked to rides
            String delFeedback = "DELETE f FROM Feedback f JOIN Rides r ON f.ride_id = r.ride_id WHERE r.rider_id = ? OR r.driver_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delFeedback)) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Delete rides where this user is rider or driver
            String delRides = "DELETE FROM Rides WHERE rider_id = ? OR driver_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delRides)) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Delete driver shifts
            String delShifts = "DELETE FROM Driver_Shifts WHERE driver_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(delShifts)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            // Delete vehicle(s) owned by driver (first fetch vehicle ids via Drivers table)
            try (PreparedStatement psv = conn.prepareStatement("SELECT vehicle_id FROM Drivers WHERE driver_id = ?")) {
                psv.setInt(1, userId);
                try (ResultSet rs = psv.executeQuery()) {
                    while (rs.next()) {
                        int vid = rs.getInt("vehicle_id");
                        if (vid > 0) {
                            try (PreparedStatement pd = conn
                                    .prepareStatement("DELETE FROM Vehicles WHERE vehicle_id = ?")) {
                                pd.setInt(1, vid);
                                pd.executeUpdate();
                            }
                        }
                    }
                }
            }

            // Delete driver/rider records
            try (PreparedStatement psd = conn.prepareStatement("DELETE FROM Drivers WHERE driver_id = ?")) {
                psd.setInt(1, userId);
                psd.executeUpdate();
            }
            try (PreparedStatement psr = conn.prepareStatement("DELETE FROM Riders WHERE rider_id = ?")) {
                psr.setInt(1, userId);
                psr.executeUpdate();
            }

            // Finally delete user
            try (PreparedStatement psu = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
                psu.setInt(1, userId);
                psu.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            System.err.println("Error deleting user and related data: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
