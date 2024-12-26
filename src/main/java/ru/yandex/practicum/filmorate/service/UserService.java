package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user) throws UserNotFoundException;

    void deleteUser(int userId);

    User getUserById(int id) throws UserNotFoundException;

    List<User> getAllUsers();

    void addFriend(int userId, int friendId) throws UserNotFoundException;

    void deleteFriend(int userId, int friendId) throws UserNotFoundException;

    List<User> getUserFriends(int userId) throws UserNotFoundException;

    List<User> getCommonFriends(int userId, int otherUserId) throws UserNotFoundException;
}