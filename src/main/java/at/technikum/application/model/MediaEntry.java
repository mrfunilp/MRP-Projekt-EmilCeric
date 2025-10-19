package at.technikum.application.model;

import java.util.ArrayList;
import java.util.List;

public class MediaEntry {
    private String id;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private String creatorUsername;

    public MediaEntry() {
        this.genres = new ArrayList<>();
        this.id = generateId(); // ID auch im Konstruktor generieren + Setter
    }

    public MediaEntry(String title, String description, MediaType mediaType, int releaseYear, String creatorUsername) {
        this();
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.creatorUsername = creatorUsername;
    }

    private String generateId() {
        return "media-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    public String getId() { return id; }
    public void setId(String id) {
        // Probleme beim testen mit Postman deshalb doppelt
        if (id != null) {
            this.id = id;
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public int getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(int ageRestriction) { this.ageRestriction = ageRestriction; }
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

}