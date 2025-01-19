package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Film with id " + film.getId() + " not found.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int filmId) throws FilmNotFoundException {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Film with id " + filmId + " not found.");
        }
        films.remove(filmId);
    }

    @Override
    public Film getFilmById(int filmId) throws FilmNotFoundException {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Film with id " + filmId + " not found.");
        }
        return films.get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) throws FilmNotFoundException {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) throws FilmNotFoundException {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}