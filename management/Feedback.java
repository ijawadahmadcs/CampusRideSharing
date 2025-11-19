// Feedback.java - Class to store and display ride feedback

public class Feedback {

    private int feedbackId;   // Unique feedback ID
    private int rideId;       // Ride ID this feedback belongs to
    private int rating;       // Rating 1-5
    private String comments;  // User comments

    // Constructor with validation for rating
    public Feedback(int feedbackId, int rideId, int rating, String comments) {
        this.feedbackId = feedbackId;
        this.rideId = rideId;
        setRating(rating);  // validate rating
        this.comments = comments;
    }

    // Getters and Setters
    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getRating() { return rating; }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            System.out.println("Invalid rating! Must be between 1 and 5. Default set to 3");
            this.rating = 3;  // default value
        }
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    // Display feedback in a readable format
    public void displayFeedback() {
        System.out.println("\n----- FEEDBACK -----");
        System.out.println("Feedback ID: " + feedbackId);
        System.out.println("Ride ID: " + rideId);
        System.out.println("Rating: " + rating + "/5 " + getStars());
        System.out.println("Comments: " + comments);
        System.out.println("--------------------\n");
    }

    // Private method to show stars for rating
    private String getStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★"); // filled star
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆"); // empty star
        }
        return stars.toString();
    }

    // For debugging or console print
    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", rideId=" + rideId +
                ", rating=" + rating +
                ", comments='" + comments + '\'' +
                '}';
    }
}
