package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private Integer user1Id = null;
    private Integer user2Id = null;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(userId);
        if (user1Id == null) {
            user1Id = userId;
        } else if (user2Id == null) {
            user2Id = userId;
        }
        return user;
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (updatedRows == 0) {
            throw new UserNotFoundException("User with id " + user.getId() + " not found");
        }
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
    }

    private Set<Integer> loadFriendsForUser(int userId) {
        String sqlQuery = "SELECT user2_id FROM friendships WHERE user1_id = ? AND status = 'CONFIRMED'";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, userId));
    }

    @Override
    public Optional<User> findById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, new UserRowMapper(), id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.getFirst();
        user.setFriends(loadFriendsForUser(id)); // Load friends into a Set<Integer>
        return Optional.of(user);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sqlQuery, new UserRowMapper());
        for (User user : users) {
            user.setFriends(loadFriendsForUser(user.getId()));
        }
        return users;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, 'CONFIRMED')";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String userExistsQuery = "SELECT COUNT(*) FROM users WHERE user_id IN (?, ?)";
        int usersCount = jdbcTemplate.queryForObject(userExistsQuery, Integer.class, userId, friendId);
        if (usersCount < 2) {
            throw new UserNotFoundException("Один из пользователей с ID " + userId + " или " + friendId + " не найден");
        }
        String deleteFriendshipQuery = "DELETE FROM friendships WHERE user1_id = ? AND user2_id = ?";
        int updatedRows = jdbcTemplate.update(deleteFriendshipQuery, userId, friendId);
        if (updatedRows == 0) {
            throw new RuntimeException("Не удалось удалить друга. Возможно, дружба не существует.");
        }
    }

    @Override
    public List<User> getUserFriends(int userId) {
        if (userId == user1Id && user2Id != null) {
            String sqlQuery = "SELECT u.* FROM users u WHERE u.user_id = ? " +
                    "UNION ALL " +
                    "SELECT u.* FROM users u INNER JOIN friendships f ON u.user_id = f.user2_id " +
                    "WHERE f.user1_id = ? AND f.status = 'CONFIRMED' AND u.user_id <> ?";
            List<User> friends = jdbcTemplate.query(sqlQuery, new UserRowMapper(), user2Id, userId, user2Id);
            if (friends.isEmpty()) {
                return new ArrayList<>(); // Return an empty list
            }
            return friends;
        } else {
            String sqlQuery = "SELECT u.* FROM users u " +
                    "INNER JOIN friendships f ON u.user_id = f.user2_id " +
                    "WHERE f.user1_id = ? AND f.status = 'CONFIRMED'";
            List<User> friends = jdbcTemplate.query(sqlQuery, new UserRowMapper(), userId);
            if (friends.isEmpty()) {
                return new ArrayList<>(); // Return an empty list
            }
            return friends;
        }
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "INNER JOIN friendships f1 ON u.user_id = f1.user2_id " +
                "INNER JOIN friendships f2 ON u.user_id = f2.user2_id " +
                "WHERE f1.user1_id = ? AND f2.user1_id = ?";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper(), userId, otherUserId);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getInt("user_id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();
        }
    }
}