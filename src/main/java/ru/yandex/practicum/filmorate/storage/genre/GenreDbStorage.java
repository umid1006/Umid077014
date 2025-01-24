package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper(), id);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genres.getFirst());
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, new GenreRowMapper());
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