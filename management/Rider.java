// Rider.java - Demonstrates Inheritance (from User class)
public class Rider extends User {

    private double balance; // Wallet balance in PKR

    // Constructor
    public Rider(int userId, String name, String email, String password) {
        super(userId, name, email, password, "Rider");
        this.balance = 0.0; // Default wallet balance
    }

    // =======================
    // Getters and Setters
    // =======================
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    // Add money to wallet
    public void addBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("PKR " + amount + " added to wallet. New balance: PKR " +
                               String.format("%.2f", balance));
        } else {
            System.out.println("Invalid amount!");
        }
    }

    // Deduct money from wallet
    public boolean deductBalance(double amount) {
        if (amount > 0 && balance >= amount) {
            this.balance -= amount;
            return true;
        } else {
            System.out.println("Insufficient balance or invalid amount!");
            return false;
        }
    }

    // =======================
    // Abstract methods from User
    // =======================
    @Override
    public void displayProfile() {
        System.out.println("\n====== RIDER PROFILE ======");
        System.out.println("Rider ID: " + getUserId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Wallet Balance: PKR " + String.format("%.2f", balance));
        System.out.println("===========================\n");
    }

    @Override
    public void updateProfile(String name, String email) {
        setName(name);
        setEmail(email);
        System.out.println("Rider profile updated successfully!");
    }

    // Simple toString for debugging
    @Override
    public String toString() {
        return "Rider{" +
                "userId=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", balance=PKR " + String.format("%.2f", balance) +
                '}';
    }
}
