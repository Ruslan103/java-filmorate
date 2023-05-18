package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    UserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        return  inMemoryUserStorage.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
   return inMemoryUserStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
      return inMemoryUserStorage.getUsers();
    }

}
