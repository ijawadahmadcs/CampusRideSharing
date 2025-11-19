import java.sql.*;

public class PaymentDAO {

    // Create a payment record
    public int createPayment(int rideId, double amount, String method, String status) {
        String sql = "INSERT INTO Payments (ride_id, amount, payment_method, payment_status) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, rideId);
            stmt.setDouble(2, amount);
            stmt.setString(3, method);
            stmt.setString(4, status);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            } else {
                System.err.println("createPayment: no rows affected when inserting payment for ride " + rideId);
            }

        } catch (SQLException e) {
            // Handle duplicate ride_id (unique constraint) by updating existing payment
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                try (PreparedStatement find = conn.prepareStatement("SELECT payment_id FROM Payments WHERE ride_id = ?")) {
                    find.setInt(1, rideId);
                    try (ResultSet found = find.executeQuery()) {
                        if (found.next()) {
                            int existingId = found.getInt(1);
                            try (PreparedStatement upd = conn.prepareStatement(
                                    "UPDATE Payments SET amount = ?, payment_method = ?, payment_status = ? WHERE payment_id = ?")) {
                                upd.setDouble(1, amount);
                                upd.setString(2, method);
                                upd.setString(3, status);
                                upd.setInt(4, existingId);
                                int urows = upd.executeUpdate();
                                if (urows > 0) return existingId;
                            }
                        }
                    }
                } catch (SQLException ex2) {
                    System.err.println("Error updating existing payment after duplicate key: " + ex2.getMessage());
                }
            } else {
                System.err.println("Error creating payment (rideId=" + rideId + "): " + e.getMessage());
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            // Do NOT close the shared connection here; DatabaseConfig manages it.
        }
        return -1;
    }

    // Update payment status
    public boolean updatePaymentStatus(int paymentId, String status) {
        String sql = "UPDATE Payments SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }
        return false;
    }
}
