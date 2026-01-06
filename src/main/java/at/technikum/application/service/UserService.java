package at.technikum.application.service;

import at.technikum.application.database.UserRepository;
import at.technikum.application.model.UserProfile;
import at.technikum.application.model.LeaderboardEntry;
import java.util.List;

public class UserService implements UserServiceInterface {  // Stelle sicher: "implements UserServiceInterface"
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfile getUserProfile(String username) {
        return userRepository.getUserProfile(username);
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        return userRepository.getLeaderboard();
    }
}