package at.technikum.application.service;

import at.technikum.application.model.Rating;
import java.util.List;

public interface RatingServiceInterface {
    Rating createRating(Rating rating);
    Rating getRating(String ratingId);
    List<Rating> getRatingsByMedia(String mediaId);
    List<Rating> getRatingsByUser(String username);
    Rating getUserRatingForMedia(String username, String mediaId);
    Rating updateRating(Rating rating, String username);
    boolean deleteRating(String ratingId, String username);
    boolean likeRating(String ratingId);
    boolean confirmComment(String ratingId, String username);
    double getAverageRating(String mediaId);
    int getRatingCount(String mediaId);
}