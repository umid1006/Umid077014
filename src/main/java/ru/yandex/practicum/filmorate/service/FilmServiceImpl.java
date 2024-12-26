package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        getFilmById(film.getId()); // Проверка наличия фильма
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film getFilmById(int id) throws FilmNotFoundException {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        Film film = getFilmById(filmId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public void deleteLike(int filmId, int userId) throws FilmNotFoundException, UserNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        Film film = getFilmById(filmId);
        film.deleteLike(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilm(int filmId) throws FilmNotFoundException {
        filmStorage.deleteFilm(filmId);
    }
}