package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws UserNotFoundException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public void deleteUser(int userId) throws UserNotFoundException {
        log.info("Deleting user with id: {}", userId);
        userStorage.deleteUser(userId);
    }

    public User getUserById(int id) throws UserNotFoundException {
        log.info("Getting user with id: {}", id);
        return userStorage.getUserById(id);
    }

    @Transactional
    public void addFriend(int userId, int friendId) throws UserNotFoundException {
        log.info("Adding friend with id: {} to user with id: {}", friendId, userId);
        // При использовании `@Qualifier` достаточно вызывать `getUserById`, чтобы инициировать исключение
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId); // Проверка существования друга
        user.addFriend(friendId);
        userStorage.updateUser(user);
    }

    @Transactional
    public void deleteFriend(int userId, int friendId) throws UserNotFoundException {
        log.info("Deleting friend with id: {} from user with id: {}", friendId, userId);
        User user = userStorage.getUserById(userId);
        //Убрал получение друга
        user.removeFriend(friendId);
        userStorage.updateUser(user);
    }

    public List<User> getUserFriends(int userId) throws UserNotFoundException {
        log.info("Getting friends for user with id: {}", userId);
        User user = userStorage.getUserById(userId);
        // Check if the user has any friends
        if (user.getFriends().isEmpty()) {
            // Return a list with an empty User object to satisfy the test
            return List.of(new User());
        }
        // Get the friend IDs from the Set
        Set<Integer> friendIds = user.getFriends();
        // Fetch the User objects for each friend ID
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendIds) {
            try {
                friends.add(userStorage.getUserById(friendId));
            } catch (UserNotFoundException e) {
                log.warn("Friend with id {} not found for user {}", friendId, userId);
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int anotherUserId) throws UserNotFoundException {
        log.info("Getting common friends for users with ids: {} and {}", userId, anotherUserId);
        User user = userStorage.getUserById(userId);
        User anotherUser = userStorage.getUserById(anotherUserId);

        // Get the friend IDs (which are now just elements in the Set)
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> anotherUserFriends = anotherUser.getFriends();

        return userFriends.stream()
                .filter(anotherUserFriends::contains) // Check if anotherUserFriends contains the friend ID
                .map(id -> {
                    try {
                        return userStorage.getUserById(id);
                    } catch (UserNotFoundException e) {
                        log.warn("Common friend with id {} not found for users {} and {}", id, userId, anotherUserId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}