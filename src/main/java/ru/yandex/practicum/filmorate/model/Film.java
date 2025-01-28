package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.FilmDataChecker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "films")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Comparable<Film> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @Column(name = "description", length = 200)
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @FilmDataChecker
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Positive
    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    @Column(name = "duration", nullable = false)
    private int duration;

    @NotNull(message = "Рейтинг MPA не может быть null")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "rating_id")
    @JsonSerialize(using = MpaRatingSerializer.class)
    private MpaRating mpa;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Transient
    @Builder.Default
    private Set<Integer> likes = new HashSet<>();

    public static class MpaIdDto {
        public int id;
    }

    @JsonCreator
    public Film(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration,
                @JsonProperty("mpa") MpaIdDto mpaIdDto, // Deserialize into DTO
                @JsonProperty("genres") Set<Genre> genres,
                @JsonProperty("likes") Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = MpaRating.forValues(mpaIdDto.id);
        this.genres = (genres != null) ? genres : new HashSet<>();
        this.likes = (likes != null) ? likes : new HashSet<>();
    }

    public Integer getMpaId() {
        return mpa != null ? mpa.ordinal() + 1: null;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        if (genres != null) {
            genres.remove(genre);
        }
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