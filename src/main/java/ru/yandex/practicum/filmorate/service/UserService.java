package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Slf4j
public class UserService {
    @Autowired
    private UserStorage inMemoryUserStorage;
    @Autowired
    private UserDbStorage userDbStorage;

    //метод добавления в друзья ТЗ 10
//    public void addFriend(long userId, long friendId) {
//        User user = inMemoryUserStorage.getUserForId(userId);
//        User friend = inMemoryUserStorage.getUserForId(friendId);
//        if (user == null || friend == null) {
//            log.error("Не верно указан id одного из пользователей");
//            throw new UserNotFoundException("Пользователь не найден");
//        }
//        user.getFriends().add(friendId);
//        friend.getFriends().add(userId);
//    }

    public void addFriend(long userId, long friendId) {
        User user = userDbStorage.getUserForId(userId);
        User friend = userDbStorage.getUserForId(friendId);
        if (user == null || friend == null) {
            log.error("Не верно указан id одного из пользователей");
            throw new UserNotFoundException("Пользователь не найден");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }
    public void deleteFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserForId(userId);
        User friend = inMemoryUserStorage.getUserForId(friendId);
        if (user == null || friend == null) {
            log.error("Не верно указан id одного из пользователей");
            throw new UserNotFoundException("Пользователь не найден");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(long userId) {
        User user = inMemoryUserStorage.getUserForId(userId);
        List<User> friends = new ArrayList<>();
        for (long friendId : user.getFriends()) {
            friends.add(inMemoryUserStorage.getUserForId(friendId));
        }
        return friends;
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
