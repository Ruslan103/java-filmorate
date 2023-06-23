package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Slf4j
public class UserService {
    @Autowired
    private UserStorage inMemoryUserStorage;
    @Autowired
    @Qualifier ("userDbStorage")
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
            stmt.setLong(1,userId);
            stmt.setLong(2,friendId);
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
        jdbcTemplate.update(sqlQuery, userId,friendId);
    }

    public List<User> getFriends(long userId) {
        String sqlQuery = "SELECT * FROM friends";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getLong("user_id"));
            return user;
        });
    }

    public List<User> mutualFriends(long id, long otherId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = inMemoryUserStorage.getUserForId(id);
        User otherUser = inMemoryUserStorage.getUserForId(otherId);
        for (long friendId : user.getFriends()) {
            if (otherUser.getFriends().contains(friendId)) {
                User mutualFriend = inMemoryUserStorage.getUserForId(friendId);
                mutualFriends.add(mutualFriend);
            }
        }
        return mutualFriends;
    }
}
