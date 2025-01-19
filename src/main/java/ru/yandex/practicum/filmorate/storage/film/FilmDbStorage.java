package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("mpaDbStorage") MpaStorage mpaStorage, @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
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
            stmt.setInt(5, film.getMpaRating().getId());
            return stmt;
        }, keyHolder);
        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        // Сохранение жанров фильма в таблицу film_genres
        if (film.getGenres() != null) {
            String genreSqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSqlQuery, filmId, genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId());

        // Обновление жанров фильма
        String deleteGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());

        if (film.getGenres() != null) {
            String insertGenresQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(insertGenresQuery, film.getId(), genre.getId());
            }
        }
        return film;
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
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, m.rating_name AS mpa_rating_name, m.description AS mpa_rating_description " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, new FilmRowMapper(), filmId);
        if (films.isEmpty()) {
            return null;
        }
        Film film = films.get(0);

        // Загрузка жанров для фильма
        String genreSqlQuery = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "INNER JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genreSqlQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }, filmId);
        film.setGenres(new HashSet<>(genres));

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, m.rating_name AS mpa_rating_name, m.description AS mpa_rating_description " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id";
        return jdbcTemplate.query(sqlQuery, new FilmRowMapper());
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

            int ratingId = rs.getInt("rating_id");
            // Используйте MpaRating.getRatingById() для получения объекта MpaRating
            MpaRating mpaRating = MpaRating.getRatingById(ratingId);

            // Проверяем на null, так как MpaRating.getRatingById может вернуть null
            if (mpaRating != null) {
                film.setMpaRating(mpaRating);
            } else {
                // Обработка ситуации, когда рейтинг не найден.
                // Можно выбросить исключение, установить значение по умолчанию или записать в лог.
                // Например:
                throw new SQLException("Не найден рейтинг MPA с ID: " + ratingId);
            }

            return film;
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.*, m.*, COALESCE(l.likes_count, 0) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count FROM film_likes GROUP BY film_id) l ON f.film_id = l.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, new FilmRowMapper(), count);
    }
}