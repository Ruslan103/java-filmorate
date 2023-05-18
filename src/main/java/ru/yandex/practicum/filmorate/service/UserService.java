package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;

@Service
public class UserService {
   private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend (long userId, long friendId) {
        User user=inMemoryUserStorage.getUserForId(userId);
        user.setFriends(Collections.singleton(friendId));
    }

}
