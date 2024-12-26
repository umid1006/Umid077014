package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film) throws FilmNotFoundException;

    void deleteFilm(int id) throws FilmNotFoundException;

    Film getFilmById(int id) throws FilmNotFoundException;

    List<Film> getAllFilms();

    void addLike(int filmId, int userId) throws FilmNotFoundException;

    void deleteLike(int filmId, int userId) throws FilmNotFoundException;

    List<Film> getPopularFilms(int count);
}