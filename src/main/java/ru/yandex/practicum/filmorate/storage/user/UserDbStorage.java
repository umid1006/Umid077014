package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    // Здесь будут методы для работы с базой данных

    @Override
    public User addUser(User user) {
        // TODO: Реализовать добавление пользователя в БД
        return null;
    }

    @Override
    public User updateUser(User user) {
        // TODO: Реализовать обновление пользователя в БД
        return null;
    }

    @Override
    public void deleteUser(int userId) {
        // TODO: Реализовать удаление пользователя в БД
    }

    @Override
    public User getUserById(int userId) {
        // TODO: Реализовать получение пользователя по ID из БД
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        // TODO: Реализовать получение всех пользователей из БД
        return null;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // TODO: Реализовать добавление друга в БД
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        // TODO: Реализовать удаление друга из БД
    }

    @Override
    public List<User> getUserFriends(int userId) {
        // TODO: Реализовать получение друзей пользователя из БД
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        // TODO: Реализовать получение общих друзей из БД
        return null;
    }
}