package at.technikum.application.model;

public class UserProfile {
    private String username;
    private int totalRatings;
    private double averageScoreGiven;
    private String favoriteGenre;
    private int mediaEntriesCreated;
    private int favoritesCount;

    public UserProfile() {}

    public UserProfile(String username, int totalRatings, double averageScoreGiven,
                       String favoriteGenre, int mediaEntriesCreated, int favoritesCount) {
        this.username = username;
        this.totalRatings = totalRatings;
        this.averageScoreGiven = averageScoreGiven;
        this.favoriteGenre = favoriteGenre;
        this.mediaEntriesCreated = mediaEntriesCreated;
        this.favoritesCount = favoritesCount;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public double getAverageScoreGiven() { return averageScoreGiven; }
    public void setAverageScoreGiven(double averageScoreGiven) { this.averageScoreGiven = averageScoreGiven; }
    public String getFavoriteGenre() { return favoriteGenre; }
    public void setFavoriteGenre(String favoriteGenre) { this.favoriteGenre = favoriteGenre; }
    public int getMediaEntriesCreated() { return mediaEntriesCreated; }
    public void setMediaEntriesCreated(int mediaEntriesCreated) { this.mediaEntriesCreated = mediaEntriesCreated; }
    public int getFavoritesCount() { return favoritesCount; }
    public void setFavoritesCount(int favoritesCount) { this.favoritesCount = favoritesCount; }
}