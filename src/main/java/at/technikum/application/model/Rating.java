package at.technikum.application.model;


public class Rating {
    private String id;
    private String mediaId;
    private String username;
    private int stars;
    private String comment;
    private String timestamp;
    private boolean commentConfirmed;
    private int likes;

    public Rating() {
        this.timestamp = null; //aus mir unerklärlichen Gründen wird mein Timestamp aus dem DB Schema nicht übernommen
        this.commentConfirmed = false;
        this.likes = 0;
        this.id = generateId();
    }

    public Rating(String mediaId, String username, int stars, String comment) {
        this();
        this.mediaId = mediaId;
        this.username = username;
        this.stars = stars;
        this.comment = comment;
    }

    private String generateId() {
        return "rating-" + System.currentTimeMillis() + "-" + Math.random();
    }

    public void like() {
        this.likes++;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getStars() { return stars; }
    public void setStars(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
        this.stars = stars;
    }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public boolean isCommentConfirmed() { return commentConfirmed; }
    public void setCommentConfirmed(boolean commentConfirmed) { this.commentConfirmed = commentConfirmed; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}