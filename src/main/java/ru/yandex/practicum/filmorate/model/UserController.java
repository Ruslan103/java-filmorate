package ru.yandex.practicum.filmorate.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {
    int id = 0;
    HashMap<Integer, User> users = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            logger.error("Неверный email: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            logger.error("Неверный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            logger.error("Неверная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        id++;
        user.setId(id);
        users.put(id, user);
        logger.info("Добавлен новый пользователь: {}", user.getName());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            logger.info("Пользователь обновлен: {}", user.getName());
        } else {
            logger.error("Пользователь не найден: {}", id);
            throw new ValidationException("Пользователь не найден");
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        logger.debug("получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }
}
