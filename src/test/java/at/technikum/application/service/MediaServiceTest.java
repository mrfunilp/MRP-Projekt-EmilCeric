package at.technikum.application.service;

import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.MediaType;
import at.technikum.application.database.MediaRepository;
import at.technikum.application.database.UserRepository;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class MediaServiceTest {
    private MediaService mediaService;
    private AuthService authService;
    private String testUser;

    @Before
    public void setUp() {
        UserRepository userRepository = new UserRepository();
        MediaRepository mediaRepository = new MediaRepository();

        mediaService = new MediaService(mediaRepository);
        authService = new AuthService(userRepository);

        testUser = "testuser_" + System.currentTimeMillis();
        authService.register(testUser, "password123");
    }

    @Test
    public void testCreateMedia() {
        MediaEntry media = new MediaEntry();
        media.setTitle("Test Movie");
        media.setDescription("Test Description");
        media.setMediaType(MediaType.MOVIE);
        media.setReleaseYear(2023);
        media.setCreatorUsername(testUser);

        MediaEntry created = mediaService.createMedia(media);

        assertNotNull("Media should be created", created);
        assertNotNull("Media should have ID", created.getId());
        assertEquals("Title should match", "Test Movie", created.getTitle());
    }

    @Test
    public void testGetMedia() {
        MediaEntry media = new MediaEntry();
        media.setTitle("Get Test Movie");
        media.setDescription("Description");
        media.setMediaType(MediaType.MOVIE);
        media.setCreatorUsername(testUser);
        MediaEntry created = mediaService.createMedia(media);

        // Act
        MediaEntry retrieved = mediaService.getMedia(created.getId());

        // Assert
        assertNotNull("Media should be retrieved", retrieved);
        assertEquals("IDs should match", created.getId(), retrieved.getId());
        assertEquals("Titles should match", created.getTitle(), retrieved.getTitle());
    }

    @Test
    public void testGetAllMedia() {
        // Arrange - Mehrere Media erstellen
        for (int i = 0; i < 3; i++) {
            MediaEntry media = new MediaEntry();
            media.setTitle("Movie " + i);
            media.setMediaType(MediaType.MOVIE);
            media.setCreatorUsername(testUser);
            mediaService.createMedia(media);
        }

        List<MediaEntry> allMedia = mediaService.getAllMedia();

        assertNotNull("List should not be null", allMedia);
        assertTrue("Should have at least 3 media", allMedia.size() >= 3);
    }

    @Test
    public void testDeleteMedia() {
        MediaEntry media = new MediaEntry();
        media.setTitle("Delete Test");
        media.setMediaType(MediaType.MOVIE);
        media.setCreatorUsername(testUser);
        MediaEntry created = mediaService.createMedia(media);

        boolean deleted = mediaService.deleteMedia(created.getId(), testUser);

        assertTrue("Media should be deleted", deleted);

        MediaEntry afterDelete = mediaService.getMedia(created.getId());
        assertNull("Media should not exist after delete", afterDelete);
    }
}