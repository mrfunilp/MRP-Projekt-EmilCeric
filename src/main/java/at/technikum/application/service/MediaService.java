package at.technikum.application.service;

import at.technikum.application.database.MediaRepository;
import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.MediaSearch;
import java.util.List;

public class MediaService implements MediaServiceInterface {
    private MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public MediaEntry createMedia(MediaEntry media) {
        if (mediaRepository.createMedia(media)) {
            return media;
        }
        return null;
    }

    @Override
    public MediaEntry getMedia(String mediaId) {
        return mediaRepository.getMediaById(mediaId);
    }

    @Override
    public List<MediaEntry> getAllMedia() {
        return mediaRepository.getAllMedia();
    }

    @Override
    public MediaEntry updateMedia(String mediaId, MediaEntry updatedMedia, String username) {
        updatedMedia.setId(mediaId);
        updatedMedia.setCreatorUsername(username);

        if (mediaRepository.updateMedia(updatedMedia)) {
            return updatedMedia;
        }
        return null;
    }

    @Override
    public boolean deleteMedia(String mediaId, String username) {
        return mediaRepository.deleteMedia(mediaId, username);
    }

    @Override
    public List<MediaEntry> searchMedia(MediaSearch search) {
        return mediaRepository.searchMedia(search);
    }
}