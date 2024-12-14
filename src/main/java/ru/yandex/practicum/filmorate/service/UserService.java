package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(nextId++); // Generate and set a unique ID
        users.put(user.getId(), user); // Add the user to the HashMap
        return user;
    }

    public User updateUser(User user) throws FilmNotFoundException {
        if (user.getId() == 0) {
            throw new ValidationException("ID пользователя должен быть указан");
        }
        User existingUser = users.values().stream() // Get a stream of the values (users)
                .filter(u -> u.getId() == user.getId())
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Пользователь с ID " + user.getId() + " не найден"));

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        } else {
            existingUser.setName(existingUser.getLogin());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }
        return existingUser;
    }

    public void getUserById(int id) { // throws FilmNotFoundException
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден"); // Changed exception type
        }
        users.get(id);
    }

    private int generateId() {
        return nextId++;
    }
}