package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Service("userService")
@Data
@Slf4j
public class UserService {
    //    @Autowired
//    private UserStorage inMemoryUserStorage;
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserForId(userId);
        User friend = userStorage.getUserForId(friendId);
        if (user == null || friend == null) {
            log.error("Не верно указан id одного из пользователей");
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, friendId);
            return stmt;
        });
    }

    public void deleteFriend(long userId, long friendId) {
//        User user = userStorage.getUserForId(userId);
//        User friend = userStorage.getUserForId(friendId);
//        if (user == null || friend == null) {
//            log.error("Не верно указан id одного из пользователей");
//            throw new UserNotFoundException("Пользователь не найден");
//        }
//        user.getFriends().remove(friendId);
//        friend.getFriends().remove(userId);
        String sqlQuery = "delete from friends where user_id = ? AND friend_Id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    //    public List<User> getFriends(long userId) {
//        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet ("SELECT friend_id FROM friends WHERE user_id = ?");
//        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
//            long friendId = rs.getLong("friend_id");
//            User friend = userStorage.getUserForId(friendId);
//            return friend;
//        });
//    }
    public List<User> getFriends(long userId) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT friend_id FROM friends WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(query, userId);
        while (userRows.next()) {
            long friendId = userRows.getLong("friend_id");
            User friend = userStorage.getUserForId(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public List<User> mutualFriends(long userId, long friendId) {
        String sqlQuery = "SELECT f1.friend_id\n" +
                "FROM friends AS f1\n" +
                "INNER JOIN friends AS f2 ON f1.friend_id = f2.friend_id\n" +
                "WHERE f1.user_id = " + userId + " AND f2.user_id = " + friendId;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            long Id = rs.getLong("friend_id");
            User friend = userStorage.getUserForId(Id);
            return friend;
        });
    }
}
