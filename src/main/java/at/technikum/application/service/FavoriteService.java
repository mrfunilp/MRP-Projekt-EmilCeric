package at.technikum.application.service;

import at.technikum.application.database.FavoriteRepository;
import at.technikum.application.database.MediaRepository;
import at.technikum.application.model.MediaEntry;
import java.util.List;

public class FavoriteService implements FavoriteServiceInterface {
    private FavoriteRepository favoriteRepository;
    private MediaRepository mediaRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public boolean addFavorite(String username, String mediaId) {
        if (mediaRepository.getMediaById(mediaId) == null) {
            return false;
        }
        return favoriteRepository.addFavorite(username, mediaId);
    }

    @Override
    public boolean removeFavorite(String username, String mediaId) {
        return favoriteRepository.removeFavorite(username, mediaId);
    }

    @Override
    public boolean isFavorite(String username, String mediaId) {
        return favoriteRepository.isFavorite(username, mediaId);
    }

    @Override
    public List<MediaEntry> getUserFavorites(String username) {
        return favoriteRepository.getUserFavorites(username);
    }

    @Override
    public int getFavoriteCount(String mediaId) {
        return favoriteRepository.getFavoriteCount(mediaId);
    }
}