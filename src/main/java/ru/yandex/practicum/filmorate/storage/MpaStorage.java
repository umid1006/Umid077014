package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<MpaRating> getMpaRatingById(int id); // Изменено
    List<MpaRating> getAllMpaRatings();
}