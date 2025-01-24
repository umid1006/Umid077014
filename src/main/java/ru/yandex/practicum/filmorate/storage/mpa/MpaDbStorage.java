package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE rating_id = ?", id);
        if (mpaRows.next()) {
            // Используем MpaRating.valueOfName для получения экземпляра enum по имени
            return Optional.ofNullable(MpaRating.valueOfName(mpaRows.getString("rating_name")));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        List<MpaRating> mpaRatings = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        while (mpaRows.next()) {
            // Используем MpaRating.valueOfName для получения экземпляра enum по имени
            mpaRatings.add(MpaRating.valueOfName(mpaRows.getString("rating_name")));
        }
        return mpaRatings;
    }
}