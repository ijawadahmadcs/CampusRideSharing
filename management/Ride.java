// Ride.java - Demonstrates Aggregation (Rider, Driver, Route)

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ride {

    private int rideId;          // Unique ride ID
    private Rider rider;         // Rider object (aggregation)
    private Driver driver;       // Driver object (aggregation)
    private Route route;         // Route object (aggregation)
    private double fare;         // Fare in PKR
    private String status;       // Pending, Ongoing, Completed, Cancelled
    private LocalDateTime rideTime; // Ride timestamp

    // Constructor
    public Ride(int rideId, Rider rider, Driver driver, Route route, double fare) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.route = route;
        this.fare = fare;
        this.status = "Pending"; // Default status
        this.rideTime = LocalDateTime.now(); // Current time
    }

    // =======================
    // Getters and Setters
    // =======================
    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public Rider getRider() { return rider; }
    public void setRider(Rider rider) { this.rider = rider; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRideTime() { return rideTime; }
    public void setRideTime(LocalDateTime rideTime) { this.rideTime = rideTime; }

    // =======================
    // Ride Operations
    // =======================

    // Start the ride
    public void startRide() {
        if (status.equals("Pending")) {
            status = "Ongoing";
            System.out.println("Ride started!");
        } else {
            System.out.println("Cannot start ride. Current status: " + status);
        }
    }

    // Complete the ride and add earnings to driver
    public void completeRide() {
        if (status.equals("Ongoing")) {
            status = "Completed";
            driver.addEarnings(fare);
            System.out.println("Ride completed! Driver earned: PKR " + String.format("%.2f", fare));
        } else {
            System.out.println("Cannot complete ride. Current status: " + status);
        }
    }

    // Cancel the ride
    public void cancelRide() {
        if (!status.equals("Completed")) {
            status = "Cancelled";
            System.out.println("Ride cancelled.");
        } else {
            System.out.println("Cannot cancel a completed ride.");
        }
    }

    // Display ride details
    public void displayRideInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n======= RIDE DETAILS =======");
        System.out.println("Ride ID: " + rideId);
        System.out.println("Rider: " + rider.getName());
        System.out.println("Driver: " + driver.getName());
        System.out.println("Route: " + route);
        System.out.println("Fare: PKR " + String.format("%.2f", fare));
        System.out.println("Status: " + status);
        System.out.println("Time: " + rideTime.format(formatter));
        System.out.println("============================\n");
    }

    // Simple toString for debugging
    @Override
    public String toString() {
        return "Ride{" +
                "rideId=" + rideId +
                ", rider=" + rider.getName() +
                ", driver=" + driver.getName() +
                ", fare=PKR " + fare +
                ", status='" + status + '\'' +
                '}';
    }
}
