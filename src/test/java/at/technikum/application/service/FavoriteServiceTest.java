package at.technikum.application.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import at.technikum.application.database.FavoriteRepository;
import at.technikum.application.database.MediaRepository;
import at.technikum.application.database.UserRepository;

public class FavoriteServiceTest {
    private FavoriteService favoriteService;
    private MediaService mediaService;
    private AuthService authService;
    private String testUser;
    private String mediaId;

    @Before
    public void setUp() {
        UserRepository userRepository = new UserRepository();
        MediaRepository mediaRepository = new MediaRepository();
        FavoriteRepository favoriteRepository = new FavoriteRepository();

        authService = new AuthService(userRepository);
        mediaService = new MediaService(mediaRepository);
        favoriteService = new FavoriteService(favoriteRepository, mediaRepository);

        testUser = "favoriteuser_" + System.currentTimeMillis();
        authService.register(testUser, "password123");

        var media = new at.technikum.application.model.MediaEntry();
        media.setTitle("Favorite Test Movie");
        media.setMediaType(at.technikum.application.model.MediaType.MOVIE);
        media.setCreatorUsername(testUser);
        var createdMedia = mediaService.createMedia(media);
        mediaId = createdMedia.getId();
    }

    @Test
    public void testAddFavorite() {
        boolean added = favoriteService.addFavorite(testUser, mediaId);

        assertTrue("Should add to favorites", added);
        assertTrue("Should be in favorites", favoriteService.isFavorite(testUser, mediaId));
    }

    @Test
    public void testRemoveFavorite() {
        favoriteService.addFavorite(testUser, mediaId);

        boolean removed = favoriteService.removeFavorite(testUser, mediaId);

        assertTrue("Should remove from favorites", removed);
        assertFalse("Should not be in favorites", favoriteService.isFavorite(testUser, mediaId));
    }

    @Test
    public void testGetUserFavorites() {
        favoriteService.addFavorite(testUser, mediaId);

        var favorites = favoriteService.getUserFavorites(testUser);

        assertNotNull("Favorites list should not be null", favorites);
        assertFalse("Favorites list should not be empty", favorites.isEmpty());
        assertEquals("Should contain the media", mediaId, favorites.get(0).getId());
    }

    @Test
    public void testAddFavoriteToNonExistentMedia() {
        String nonExistentMediaId = "non-existent-media-12345";

        boolean added = favoriteService.addFavorite(testUser, nonExistentMediaId);

        assertFalse("Should not add favorite for non-existent media", added);
    }
}