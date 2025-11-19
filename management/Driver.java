// Driver.java - Demonstrates Inheritance and Composition

public class Driver extends User {

    private String licenseNumber;   // Driver ka license number
    private double totalEarnings;   // Total earnings in PKR
    private Vehicle vehicle;        // Driver ka vehicle (Composition)

    // Constructor
    public Driver(int userId, String name, String email, String password,
                  String licenseNumber) {
        super(userId, name, email, password, "Driver"); // Call parent constructor
        this.licenseNumber = licenseNumber;
        this.totalEarnings = 0.0; // Starting earnings
    }

    // =======================
    // Getters and Setters
    // =======================
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    // Add amount to total earnings
    public void addEarnings(double amount) {
        this.totalEarnings += amount;
    }

    // =======================
    // Implement abstract methods from User
    // =======================

    @Override
    public void displayProfile() {
        System.out.println("\n====== DRIVER PROFILE ======");
        System.out.println("Driver ID: " + getUserId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("License Number: " + licenseNumber);
        System.out.println("Total Earnings: PKR " + String.format("%.2f", totalEarnings));
        if (vehicle != null) {
            System.out.println("Vehicle: " + vehicle.getModel() + " (" + vehicle.getPlateNumber() + ")");
        } else {
            System.out.println("Vehicle: Not assigned");
        }
        System.out.println("===========================\n");
    }

    @Override
    public void updateProfile(String name, String email) {
        setName(name);
        setEmail(email);
        System.out.println("Driver profile updated successfully!");
    }

    // Check if driver has a vehicle assigned
    public boolean hasVehicle() {
        return vehicle != null;
    }

    // Object info for debugging or console
    @Override
    public String toString() {
        return "Driver{" +
                "userId=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", totalEarnings=PKR " + totalEarnings +
                ", hasVehicle=" + hasVehicle() +
                '}';
    }
}
