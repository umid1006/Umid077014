package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotFoundException {
        log.info("Updating user: {}", user);
        if (user.getId() <= 0) {
            throw new ValidationException("User ID must be a positive integer");
        }
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws UserNotFoundException {
        log.info("Getting user with id: {}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) throws UserNotFoundException {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) throws UserNotFoundException {
        log.info("Adding friend with id: {} to user with id: {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) throws UserNotFoundException {
        log.info("Deleting friend with id: {} from user with id: {}", friendId, id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) throws UserNotFoundException {
        log.info("Getting friends for user with id: {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherUserId) throws UserNotFoundException {
        log.info("Getting common friends for users with ids: {} and {}", id, otherUserId);
        return userService.getCommonFriends(id, otherUserId);
    }
}