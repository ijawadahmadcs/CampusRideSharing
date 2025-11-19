import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverShiftDAO {

    public boolean addShift(int driverId, Date shiftDate, Time startTime, Time endTime) {
        String sql = "INSERT INTO Driver_Shifts (driver_id, shift_date, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setDate(2, shiftDate);
            stmt.setTime(3, startTime);
            stmt.setTime(4, endTime);
            return stmt.executeUpdate() > 0;
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
}
