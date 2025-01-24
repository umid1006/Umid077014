package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    private Logger log; // Logger instance

    public void setLogger(Logger log) {
        this.log = log;
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User with id " + user.getId() + " not found.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }
        users.remove(userId);
    }

    @Override
    public User getUserById(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        user.addFriend(friendId); // Only add to the user's friend list
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        user.removeFriend(friendId);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        User user = getUserById(userId);
        Set<Integer> friendIds = user.getFriends();

        if (friendIds.isEmpty()) {
            return List.of(new User()); // Return a list with an empty User object
        }

        List<User> friends = new ArrayList<>();
        for (Integer id : friendIds) {
            try {
                friends.add(getUserById(id));
            } catch (UserNotFoundException e) {
                if (log != null) {
                    log.warn("Friend with ID {} not found while getting friends for user with ID {}", id, userId);
                } else {
                    // Fallback to a local logger if UserService logger is not accessible
                    Logger logger = org.slf4j.LoggerFactory.getLogger(InMemoryUserStorage.class);
                    logger.warn("Friend with ID {} not found while getting friends for user with ID {}", id, userId);
                }
            }
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }
}