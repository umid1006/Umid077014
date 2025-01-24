package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }


    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setString(5, film.getMpaRating().name()); // Store the name of the enum
            return stmt;
        }, keyHolder);
        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);
        // Сохранение жанров фильма в таблицу film_genres
        saveFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().name(),
                film.getId());
        // Обновление жанров фильма
        String deleteGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());
        saveFilmGenres(film);

        return film;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() != null) {
            String genreSqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSqlQuery, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void deleteFilm(int filmId) {
        // Удаление связей с жанрами
        String deleteGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, filmId);

        // Удаление лайков
        String deleteLikesQuery = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(deleteLikesQuery, filmId);

        // Удаление фильма
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film getFilmById(int filmId) throws FilmNotFoundException {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films f " +
                "WHERE f.film_id = ?";
        List<Film> films;
        try {
            films = jdbcTemplate.query(sqlQuery, new FilmRowMapper(), filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        if (films.isEmpty()) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        Film film = films.getFirst();

        // Загрузка жанров для фильма и установка их в объект Film
        film.setGenres(loadGenresForFilm(filmId));

        return film;
    }

    private Set<Genre> loadGenresForFilm(int filmId) {
        String genreSqlQuery = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "INNER JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Integer> genreIds = jdbcTemplate.queryForList(genreSqlQuery, Integer.class, filmId);
        return genreIds.stream()
                .map(genreId -> genreStorage.getGenreById(genreId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films f ";
        List<Film> films = jdbcTemplate.query(sqlQuery, new FilmRowMapper());

        // Загрузка жанров для каждого фильма
        films.forEach(film -> film.setGenres(loadGenresForFilm(film.getId())));

        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            // Получаем строковое представление MpaRating из базы данных
            String mpaRatingString = rs.getString("rating_id");
            // Используем MpaRating.valueOf() для преобразования строки в Enum
            try {
                MpaRating mpaRating = MpaRating.valueOf(mpaRatingString);
                film.setMpaRating(mpaRating);
            } catch (IllegalArgumentException e) {
                // Обработка ситуации, когда значение в базе данных не соответствует ни одному из значений Enum
                throw new SQLException("Недопустимое значение MpaRating в базе данных: " + mpaRatingString);
            }
            return film;
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Используем конструктор Genre
            return new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            );
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.*, COALESCE(l.likes_count, 0) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count FROM film_likes GROUP BY film_id) l ON f.film_id = l.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, new FilmRowMapper(), count);
        // Загрузка жанров для каждого фильма
        for (Film film : films) {
            film.setGenres(loadGenresForFilm(film.getId()));
        }
        return films;
    }
}