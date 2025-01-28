package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.InvalidRatingException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum MpaRating {
    G(1),
    PG(2),
    PG13(3),
    R(4),
    NC17(5);

    private final int id;

    MpaRating(int id) {
        this.id = id;
    }

    @JsonCreator
    public static MpaRating forValues(@JsonProperty("id") int id) {
        for (MpaRating mpaRating : MpaRating.values()) {
            if (mpaRating.id == id) {
                return mpaRating;
            }
        }
        throw new InvalidRatingException("Invalid MPAA rating id provided.");
    }
}