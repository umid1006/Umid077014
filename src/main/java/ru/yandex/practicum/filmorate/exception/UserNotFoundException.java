package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException { // Or extend Exception if you prefer
    public UserNotFoundException(String message) {
        super(message);
    }
}