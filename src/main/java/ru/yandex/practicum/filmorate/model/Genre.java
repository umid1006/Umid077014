package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private int id;

    @Column(name = "genre_name", nullable = false, unique = true)
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}