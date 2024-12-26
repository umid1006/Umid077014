package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film) throws FilmNotFoundException;

    void deleteFilm(int filmId) throws FilmNotFoundException;

    Film getFilmById(int id) throws FilmNotFoundException;

    List<Film> getAllFilms();

    void addLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException;

    void deleteLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException;

    List<Film> getPopularFilms(int count);
}