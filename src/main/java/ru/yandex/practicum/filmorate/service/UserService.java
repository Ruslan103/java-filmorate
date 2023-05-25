package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class UserService {
    @Autowired
    private UserStorage inMemoryUserStorage;

    //метод добавления в друзья ТЗ 10
    public User addFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserForId(userId);
        User friend = inMemoryUserStorage.getUserForId(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("User not found");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return friend;

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
