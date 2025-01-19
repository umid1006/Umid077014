package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mpa")
public enum MpaRating {
    G(1, "G", "У фильма нет возрастных ограничений"),
    PG(2, "PG", "Детям рекомендуется смотреть фильм с родителями"),
    PG13(3, "PG-13", "Детям до 13 лет просмотр не желателен"),
    R(4, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17(5, "NC-17", "Лицам до 18 лет просмотр запрещён");

    @Id
    @Column(name = "rating_id")
    private final int id;

    @Column(name = "rating_name", nullable = false, unique = true)
    private final String name;

    @Column(name = "description")
    private final String description;

    MpaRating(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    MpaRating() {
        this.id = 0;
        this.name = null;
        this.description = null;
    }

    public static MpaRating getRatingById(int id) {
        for (MpaRating rating : MpaRating.values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        return null;
    }
}