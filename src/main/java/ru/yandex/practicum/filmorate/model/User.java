package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;
    @Size(min = 2, max = 255, message = "Имя должно содержать от 2 до 255 символов")
    String name;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    String login;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректного формата")
    String email;
    @NotNull(message = "Дата рождения не может быть null")
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    final Set<Integer> friends = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(int friendId) {
        friends.remove(friendId);
    }

    public boolean isFriend(int friendId) {
        return friends.contains(friendId);
    }
}