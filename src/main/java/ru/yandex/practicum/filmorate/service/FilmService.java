package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws FilmNotFoundException {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new FilmNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(int filmId) throws FilmNotFoundException {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        filmStorage.deleteFilm(filmId);
    }

    public Film getFilmById(int filmId) throws FilmNotFoundException {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        // Проверка существования фильма и пользователя делегирована хранилищу
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        // Проверка существования фильма и пользователя делегирована хранилищу
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}