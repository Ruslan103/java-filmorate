package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    //метод добавления в друзья ТЗ 10
    public void addFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserForId(userId);
        User friend = inMemoryUserStorage.getUserForId(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

    }

    public void deleteFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserForId(userId);
        User friend = inMemoryUserStorage.getUserForId(friendId);
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
}
