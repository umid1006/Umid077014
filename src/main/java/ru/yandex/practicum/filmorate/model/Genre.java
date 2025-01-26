package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "genres")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Genre {

    @Id
    @Column(name = "genre_id")
    private int id;

    @Column(name = "genre_name")
    private String name;

    @JsonCreator
    public static Genre forValues(Map<String, Object> genre) {
        if (genre == null || !genre.containsKey("id")) {
            throw new IllegalArgumentException("Invalid input for Genre");
        }
        return new Genre((int) genre.get("id"), null); // Name will be set later if needed
    }
}