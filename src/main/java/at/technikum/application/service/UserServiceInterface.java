package at.technikum.application.service;

import at.technikum.application.model.UserProfile;
import at.technikum.application.model.LeaderboardEntry;
import java.util.List;

public interface UserServiceInterface {
    UserProfile getUserProfile(String username);
    List<LeaderboardEntry> getLeaderboard();
}