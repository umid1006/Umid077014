package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FilmDataChecker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "films")
@Getter
@Setter
@ToString(exclude = "likes")
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Film implements Comparable<Film> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    @Column(name = "name", nullable = false)
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @Column(name = "description", length = 200)
    String description;

    @NotNull(message = "Дата релиза не может быть null")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @FilmDataChecker
    @Column(name = "release_date", nullable = false)
    LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    @Column(name = "duration", nullable = false)
    int duration;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "film_likes", joinColumns = @JoinColumn(name = "film_id"))
    @Column(name = "user_id")
    Set<Integer> likes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> genres = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rating_id", nullable = false)
    @NotNull(message = "Рейтинг MPA не может быть null")
    private MpaRating mpaRating;


    public Film(String name, String description, LocalDate releaseDate, int duration, Set<Genre> genres, MpaRating mpaRating) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpaRating = mpaRating;
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