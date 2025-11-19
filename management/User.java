// User.java - Abstract class for all users

public abstract class User {

    // private fields for user info
    private int userId;      
    private String name;     
    private String email;    
    private String password; 
    private String userType; // Driver or Rider

    // constructor to set user info
    public User(int userId, String name, String email, String password, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    // abstract methods - implemented by Driver/Rider
    public abstract void displayProfile(); 
    public abstract void updateProfile(String name, String email);

    // check login credentials
    public boolean authenticate(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    // simple info about user
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
