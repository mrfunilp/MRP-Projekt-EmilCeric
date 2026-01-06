package at.technikum.application.service;

import at.technikum.application.database.RatingRepository;
import at.technikum.application.database.MediaRepository;
import at.technikum.application.model.Rating;
import java.util.List;

public class RatingService implements RatingServiceInterface {
    private RatingRepository ratingRepository;
    private MediaRepository mediaRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Rating createRating(Rating rating) {
        if (mediaRepository.getMediaById(rating.getMediaId()) == null) {
            return null;
        }

        Rating existing = ratingRepository.getUserRatingForMedia(rating.getUsername(), rating.getMediaId());
        if (existing != null) {
            return null;
        }

        rating.setCommentConfirmed(false);

        if (rating.getComment() == null || rating.getComment().trim().isEmpty()) {
            rating.setCommentConfirmed(true);
        }

        if (ratingRepository.createRating(rating)) {
            return rating;
        }
        return null;
    }

    @Override
    public Rating getRating(String ratingId) {
        return ratingRepository.getRatingById(ratingId);
    }

    @Override
    public List<Rating> getRatingsByMedia(String mediaId) {
        return ratingRepository.getRatingsByMedia(mediaId);
    }

    @Override
    public List<Rating> getRatingsByUser(String username) {
        return ratingRepository.getRatingsByUser(username);
    }

    @Override
    public Rating getUserRatingForMedia(String username, String mediaId) {
        return ratingRepository.getUserRatingForMedia(username, mediaId);
    }

    @Override
    public Rating updateRating(Rating rating, String username) {
        Rating existing = ratingRepository.getRatingById(rating.getId());
        if (existing == null || !existing.getUsername().equals(username)) {
            return null;
        }

        rating.setUsername(username);

        if (rating.getComment() == null || rating.getComment().trim().isEmpty()) {
            rating.setCommentConfirmed(true);
        } else if (!rating.getComment().equals(existing.getComment())) {
            rating.setCommentConfirmed(false);
        } else {
            rating.setCommentConfirmed(existing.isCommentConfirmed());
        }

        if (ratingRepository.updateRating(rating)) {
            return rating;
        }
        return null;
    }

    @Override
    public boolean deleteRating(String ratingId, String username) {
        return ratingRepository.deleteRating(ratingId, username);
    }

    @Override
    public boolean likeRating(String ratingId) {
        return ratingRepository.likeRating(ratingId);
    }

    @Override
    public boolean confirmComment(String ratingId, String username) {
        return ratingRepository.confirmComment(ratingId, username);
    }

    @Override
    public double getAverageRating(String mediaId) {
        return ratingRepository.getAverageRating(mediaId);
    }

    @Override
    public int getRatingCount(String mediaId) {
        return ratingRepository.getRatingCount(mediaId);
    }
}