package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "genres")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    int id;

    @Column(name = "genre_name", nullable = false, unique = true)
    String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}