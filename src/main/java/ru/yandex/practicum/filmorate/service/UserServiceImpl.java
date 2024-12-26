package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        getUserById(user.getId()); // Проверка наличия пользователя
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(int userId) throws UserNotFoundException {
        userStorage.deleteUser(userId);
    }

    @Override
    public User getUserById(int id) throws UserNotFoundException {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) throws UserNotFoundException {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) throws UserNotFoundException {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public List<User> getUserFriends(int userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) throws UserNotFoundException {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}