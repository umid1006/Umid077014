package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum MpaRating {
    G(1, "G", "У фильма нет возрастных ограничений"),
    PG(2, "PG", "Детям рекомендуется смотреть фильм с родителями"),
    PG13(3, "PG-13", "Детям до 13 лет просмотр не желателен"),
    R(4, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17(5, "NC-17", "Лицам до 18 лет просмотр запрещён");

    private final int id;
    private final String name;
    private final String description;

    MpaRating(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @JsonCreator
    public static MpaRating forValues(Map<String, Object> mpaRating) {
        if (mpaRating == null) {
            throw new IllegalArgumentException("Invalid input for MpaRating");
        }
        for (MpaRating rating : MpaRating.values()) {
            if (rating.getId() == (Integer) mpaRating.get("id")) {
                return rating;
            }
        }
        return null;
    }

    public static MpaRating valueOfName(String name) {
        for (MpaRating mpa : values()) {
            if (mpa.name.equals(name)) {
                return mpa;
            }
        }
        return null;
    }

    public static MpaRating fromId(int id) {
        for (MpaRating rating : values()) {
            if (rating.id == id) {
                return rating;
            }
        }
        return null;
    }
}