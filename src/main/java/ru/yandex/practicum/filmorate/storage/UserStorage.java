package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    User getUserById(int id);

    List<User> getAllUsers();

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

    // Добавьте объявление метода findById
    Optional<User> findById(int id);
}