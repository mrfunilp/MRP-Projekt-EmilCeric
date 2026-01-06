package at.technikum.application.model;

public class LeaderboardEntry {
    private String username;
    private int ratingCount;

    public LeaderboardEntry() {}

    public LeaderboardEntry(String username, int ratingCount) {
        this.username = username;
        this.ratingCount = ratingCount;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}