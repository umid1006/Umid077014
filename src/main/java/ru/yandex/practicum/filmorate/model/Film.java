package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validation.FilmDataChecker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @FilmDataChecker
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @Getter // Add this line
    @Setter // Add this line
    private Set<Integer> likes = new HashSet<>(); // Remove the final keyword

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

    @Override
    public int compareTo(Film other) {
        // Сравниваем фильмы по количеству лайков в порядке убывания
        return Integer.compare(other.getLikesCount(), this.getLikesCount());
    }
}