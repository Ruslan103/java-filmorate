package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    public User addUser(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Неверный email: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.error("Неверный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Неверная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        id++;
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен новый пользователь: {}", user.getName());
        return user;
    }

    public User updateUser(User user) {
        long id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Пользователь обновлен: {}", user.getName());
        } else {
            log.error("Пользователь не найден: {}", id);
            throw new ValidationException("Пользователь не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        log.debug("получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    public User getUserForId(long id) {

        return users.get(id);
    }
}
