public class Route {
    private int routeId;
    private String startLocation;
    private String endLocation;
    private double distanceKm;

    // Constructor
    public Route(int routeId, String startLocation, String endLocation, double distanceKm) {
        this.routeId = routeId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        setDistanceKm(distanceKm); // use setter for validation
    }

    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative.");
        }
        this.distanceKm = distanceKm;
    }

    // Display route nicely
    public void displayRoute() {
        System.out.println("\n----- Route Info -----");
        System.out.println("Route ID: " + routeId);
        System.out.println("From: " + startLocation);
        System.out.println("To: " + endLocation);
        System.out.printf("Distance: %.2f km\n", distanceKm);
        System.out.println("---------------------\n");
    }

    @Override
    public String toString() {
        return startLocation + " → " + endLocation + " (" + String.format("%.2f km", distanceKm) + ")";
    }
}
