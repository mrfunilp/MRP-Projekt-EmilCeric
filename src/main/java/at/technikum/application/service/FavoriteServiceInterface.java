package at.technikum.application.service;

import at.technikum.application.model.MediaEntry;
import java.util.List;

public interface FavoriteServiceInterface {
    boolean addFavorite(String username, String mediaId);
    boolean removeFavorite(String username, String mediaId);
    boolean isFavorite(String username, String mediaId);
    List<MediaEntry> getUserFavorites(String username);
    int getFavoriteCount(String mediaId);
}