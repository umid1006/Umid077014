package ru.yandex.practicum.filmorate.storage;

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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
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
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        // Remove duplicates from film's genre set before saving
        if (film.getGenres() != null) {
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            film.setGenres(uniqueGenres);
            saveFilmGenres(film);
        }
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
                film.getMpa().getId(),
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

        // Удаление фильма
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film getFilmById(int filmId) throws FilmNotFoundException {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
                "GROUP_CONCAT(DISTINCT g.genre_id || ',' || g.genre_name ORDER BY g.genre_id SEPARATOR ';') AS genres, " +
                "GROUP_CONCAT(DISTINCT ul.user_id SEPARATOR ',') AS likes " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN users_likes ul ON f.film_id = ul.film_id " +
                "WHERE f.film_id = ? " +
                "GROUP BY f.film_id";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }

    private Set<Genre> loadGenresForFilm(int filmId) {
        String genreSqlQuery = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "INNER JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(genreSqlQuery, new FilmDbStorage.GenreRowMapper(), filmId));
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
                "GROUP_CONCAT(DISTINCT g.genre_id || ',' || g.genre_name ORDER BY g.genre_id SEPARATOR ';') AS genres, " +
                "GROUP_CONCAT(DISTINCT ul.user_id SEPARATOR ',') AS likes " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN users_likes ul ON f.film_id = ul.film_id " +
                "GROUP BY f.film_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO users_likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM users_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
                "GROUP_CONCAT(DISTINCT g.genre_id || ',' || g.genre_name ORDER BY g.genre_id SEPARATOR ';') AS genres, " +
                "GROUP_CONCAT(DISTINCT ul.user_id SEPARATOR ',') AS likes, " +
                "COUNT(DISTINCT ul.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN users_likes ul ON f.film_id = ul.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = MpaRating.forValues(rs.getInt("rating_id"));

        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaRating)
                .build();

        // Обработка жанров
        String genresString = rs.getString("genres");
        if (genresString != null && !genresString.isEmpty()) {
            Set<Genre> genres = Arrays.stream(genresString.split(";"))
                    .map(s -> {
                        String[] parts = s.split(",");
                        return new Genre(Integer.parseInt(parts[0]), parts[1]);
                    })
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        // Обработка лайков
        String likesString = rs.getString("likes");
        if (likesString != null && !likesString.isEmpty()) {
            Set<Integer> likes = Arrays.stream(likesString.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            film.setLikes(likes);
        }

        return film;
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            );
        }
    }
}