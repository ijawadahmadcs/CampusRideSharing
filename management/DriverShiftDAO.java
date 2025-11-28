import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverShiftDAO {

    public boolean addShift(int driverId, Date shiftDate, Time startTime, Time endTime) {
        String insertSql = "INSERT INTO Driver_Shifts (driver_id, shift_date, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, driverId);
            insertStmt.setDate(2, shiftDate);
            insertStmt.setTime(3, startTime);
            insertStmt.setTime(4, endTime);
            int affected = insertStmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int newId = keys.getInt(1);
                        System.out.println("Shift inserted with shift_id=" + newId);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding shift: " + e.getMessage());
        }
        return false;
    }

    public List<DriverShift> getShiftsByDriver(int driverId) {
        List<DriverShift> list = new ArrayList<>();
        String sql = "SELECT * FROM Driver_Shifts WHERE driver_id = ? ORDER BY shift_date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DriverShift ds = new DriverShift(
                        rs.getInt("shift_id"),
                        rs.getInt("driver_id"),
                        rs.getDate("shift_date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                    );
                    list.add(ds);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading shifts: " + e.getMessage());
        }
        return list;
    }

    /**
     * Set the end_time for a given shift (allowing drivers to end a shift early).
     */
    public boolean endShift(int shiftId, Time endTime) {
        String sql = "UPDATE Driver_Shifts SET end_time = ? WHERE shift_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, endTime);
            stmt.setInt(2, shiftId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error ending shift: " + e.getMessage());
        }
        return false;
    }
}
