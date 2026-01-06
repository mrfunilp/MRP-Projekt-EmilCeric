package at.technikum.application.service;

import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.MediaSearch;
import java.util.List;

public interface MediaServiceInterface {
    MediaEntry createMedia(MediaEntry media);
    MediaEntry getMedia(String mediaId);
    List<MediaEntry> getAllMedia();
    MediaEntry updateMedia(String mediaId, MediaEntry updatedMedia, String username);
    boolean deleteMedia(String mediaId, String username);
    List<MediaEntry> searchMedia(MediaSearch search);
}