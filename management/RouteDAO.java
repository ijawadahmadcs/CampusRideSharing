import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {

    // Get all routes from database
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();

        Connection conn = DatabaseConfig.getConnection();
        if (conn == null) {
            System.err.println("Database connection is NULL!");
            return routes; // return empty list if no connection
        }

        try {
            String sql = "SELECT * FROM Routes"; // query all routes
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // create Route object from database row
                Route route = new Route(
                    rs.getInt("route_id"),
                    rs.getString("start_location"),
                    rs.getString("end_location"),
                    rs.getDouble("distance_km")
                );
                routes.add(route); // add route to list
            }

        } catch (SQLException e) {
            System.err.println("Error getting routes: " + e.getMessage());
        }

        return routes;
    }

    // Add new route to database
    public int addRoute(String startLocation, String endLocation, double distanceKm) {
        Connection conn = DatabaseConfig.getConnection();
        if (conn == null) {
            System.err.println("Database connection is NULL!");
            return -1;
        }

        try {
            String sql = "INSERT INTO Routes (start_location, end_location, distance_km) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, startLocation);
            stmt.setString(2, endLocation);
            stmt.setDouble(3, distanceKm);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys(); // get generated route_id
                if (rs.next()) {
                    return rs.getInt(1); // return new route id
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding route: " + e.getMessage());
        }

        return -1; // return -1 if failed
    }

    // Get route by its ID
    public Route getRouteById(int routeId) {
        Connection conn = DatabaseConfig.getConnection();
        if (conn == null) {
            System.err.println("Database connection is NULL!");
            return null;
        }

        try {
            String sql = "SELECT * FROM Routes WHERE route_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, routeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // create and return route object
                return new Route(
                    rs.getInt("route_id"),
                    rs.getString("start_location"),
                    rs.getString("end_location"),
                    rs.getDouble("distance_km")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error getting route: " + e.getMessage());
        }

        return null; // return null if not found
    }
}
