package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@Data
public class UserController {
    @Autowired
    private UserStorage inMemoryUserStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDbStorage userDbStorage;

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        return userDbStorage.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userDbStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userDbStorage.getUsers();
    }

    //    @GetMapping("/users/{id}")
//    public User getUserForId(@PathVariable long id) {
//        return inMemoryUserStorage.getUserForId(id);
//    }
    @GetMapping("/users/{id}")
    public User getUserForId(@PathVariable long id) {
        return userDbStorage.getUserForId(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> mutualFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.mutualFriends(id, otherId);
    }
}
