package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaRating getMpaRatingById(int ratingId) {
        String sqlQuery = "SELECT rating_id, rating_name, description FROM mpa WHERE rating_id = ?";
        List<MpaRating> mpaRatings = jdbcTemplate.query(sqlQuery, new MpaRatingRowMapper(), ratingId);
        if (mpaRatings.isEmpty()) {
            return null;
        }
        return mpaRatings.get(0);
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        String sqlQuery = "SELECT rating_id, rating_name, description FROM mpa";
        return jdbcTemplate.query(sqlQuery, new MpaRatingRowMapper());
    }

    private static class MpaRatingRowMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("rating_id");
            String name = rs.getString("rating_name");
            String description = rs.getString("description");
            return MpaRating.getRatingById(id);
        }
    }
}