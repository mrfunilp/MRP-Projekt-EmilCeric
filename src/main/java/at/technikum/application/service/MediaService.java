package at.technikum.application.service;

import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.MediaType;
import at.technikum.application.model.Rating;

import java.util.*;
import java.util.stream.Collectors;

public class MediaService {
    private static MediaService instance;

    //Ersatz für Datenbank (temporär)
    private Map<String, MediaEntry> mediaEntries = new HashMap<>(); // mediaId -> MediaEntry

    private MediaService() {}

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }

    public MediaEntry createMedia(MediaEntry media) {
        mediaEntries.put(media.getId(), media);
        return media;
    }

    public MediaEntry getMedia(String mediaId) {
        return mediaEntries.get(mediaId);
    }

    public List<MediaEntry> getAllMedia() {
        return new ArrayList<>(mediaEntries.values());
    }

    public MediaEntry updateMedia(String mediaId, MediaEntry updatedMedia, String username) {
        MediaEntry existing = mediaEntries.get(mediaId);
        if (existing == null || !existing.getCreatorUsername().equals(username)) {
            return null;
        }
        updatedMedia.setId(mediaId);
        updatedMedia.setCreatorUsername(username);
        mediaEntries.put(mediaId, updatedMedia);
        return updatedMedia;
    }

    public boolean deleteMedia(String mediaId, String username) {
        MediaEntry media = mediaEntries.get(mediaId);
        if (media == null || !media.getCreatorUsername().equals(username)) {
            return false;
        }
        mediaEntries.remove(mediaId);
        return true;
    }





}