package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId()); // Проверка наличия пользователя
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId); // Ensure user exists

        return user.getFriends().stream()
                .map(userStorage::getUserById) // Efficiently retrieve User objects
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}