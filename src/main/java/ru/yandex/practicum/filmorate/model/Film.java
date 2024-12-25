package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FilmDataChecker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @NotNull(message = "Дата релиза не может быть null")
    @PastOrPresent(message = "Дата релиза не может быть в будущем") // Добавьте эту аннотацию
    @FilmDataChecker
    LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    int duration;
    final Set<Integer> likes = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void deleteLike(int userId) {
        likes.remove(userId);
    }

    public int getLikesCount() {
        return likes.size();
    }

    public boolean isLikedBy(int userId) {
        return likes.contains(userId);
    }
}