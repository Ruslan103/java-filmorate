package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
public class UserController {
    UserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserForId(@PathVariable int id) {
        User user = inMemoryUserStorage.getUserForId(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity  addFriend(@PathVariable int id, @PathVariable long friendId) {
        User user = inMemoryUserStorage.getUserForId(id);
        User friend = inMemoryUserStorage.getUserForId(friendId);
        if (user == null || friend == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ValidationException("Неверный id"));
        } else {
            return ResponseEntity.ok(userService.addFriend(id, friendId));
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable long friendId) {
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
