package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws FilmNotFoundException {
        getFilmById(film.getId()); // Проверка наличия фильма
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) throws FilmNotFoundException {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    public void addLike(int filmId, int userId) throws FilmNotFoundException {
        Film film = getFilmById(filmId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(int filmId, int userId) throws FilmNotFoundException {
        Film film = getFilmById(filmId);
        film.deleteLike(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}