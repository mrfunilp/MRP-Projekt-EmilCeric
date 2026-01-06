package at.technikum.application.service;

import at.technikum.application.model.Rating;
import at.technikum.application.database.RatingRepository;
import at.technikum.application.database.MediaRepository;
import at.technikum.application.database.UserRepository;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RatingServiceTest {
    private RatingService ratingService;
    private MediaService mediaService;
    private AuthService authService;
    private String testUser;
    private String mediaId;

    @Before
    public void setUp() {
        UserRepository userRepository = new UserRepository();
        MediaRepository mediaRepository = new MediaRepository();
        RatingRepository ratingRepository = new RatingRepository();

        authService = new AuthService(userRepository);
        mediaService = new MediaService(mediaRepository);
        ratingService = new RatingService(ratingRepository, mediaRepository);

        testUser = "ratinguser_" + System.currentTimeMillis();
        authService.register(testUser, "password123");

        var media = new at.technikum.application.model.MediaEntry();
        media.setTitle("Rating Test Movie");
        media.setMediaType(at.technikum.application.model.MediaType.MOVIE);
        media.setCreatorUsername(testUser);
        var createdMedia = mediaService.createMedia(media);
        mediaId = createdMedia.getId();
    }

    @Test
    public void testCreateRating() {
        Rating rating = new Rating();
        rating.setMediaId(mediaId);
        rating.setUsername(testUser);
        rating.setStars(4);
        rating.setComment("Good movie");

        Rating created = ratingService.createRating(rating);

        assertNotNull("Rating should be created", created);
        assertEquals("Stars should match", 4, created.getStars());
        assertEquals("Comment should match", "Good movie", created.getComment());
    }

    @Test
    public void testDuplicateRating() {
        Rating rating1 = new Rating();
        rating1.setMediaId(mediaId);
        rating1.setUsername(testUser);
        rating1.setStars(4);
        ratingService.createRating(rating1);

        Rating rating2 = new Rating();
        rating2.setMediaId(mediaId);
        rating2.setUsername(testUser);
        rating2.setStars(5);
        Rating duplicate = ratingService.createRating(rating2);

        assertNull("Should not allow duplicate rating", duplicate);
    }

    @Test
    public void testLikeRating() {
        Rating rating = new Rating();
        rating.setMediaId(mediaId);
        rating.setUsername(testUser);
        rating.setStars(3);
        Rating created = ratingService.createRating(rating);

        boolean liked = ratingService.likeRating(created.getId());
        assertTrue("Rating should be liked", liked);
    }

    @Test
    public void testCreateRatingWithEmptyCommentAutoConfirms() {
        Rating rating = new Rating();
        rating.setMediaId(mediaId);
        rating.setUsername(testUser);
        rating.setStars(4);
        rating.setComment(""); // Leerer Kommentar

        Rating created = ratingService.createRating(rating);

        assertNotNull("Rating should be created", created);
        assertTrue("Empty comment should auto-confirm", created.isCommentConfirmed());
    }

    @Test
    public void testUpdateRatingWithChangedCommentNeedsReconfirmation() {
        Rating rating = new Rating();
        rating.setMediaId(mediaId);
        rating.setUsername(testUser);
        rating.setStars(3);
        rating.setComment("Original comment");
        Rating created = ratingService.createRating(rating);

        ratingService.confirmComment(created.getId(), testUser);

        created.setComment("Changed comment");

        Rating updated = ratingService.updateRating(created, testUser);

        assertNotNull("Rating should be updated", updated);
        assertFalse("Changed comment should need re-confirmation", updated.isCommentConfirmed());
    }
}