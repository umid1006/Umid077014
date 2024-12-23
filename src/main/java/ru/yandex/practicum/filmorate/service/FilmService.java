package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) throws FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilmById(int id) throws FilmNotFoundException {
        Film film = films.get(id); // Assuming 'films' is your Map or database access mechanism
        if (film == null) {
            throw new FilmNotFoundException("Film with ID " + id + " not found.");
        }
        return film;
    }
}