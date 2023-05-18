package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    public User addUser(@Valid @RequestBody User user);
    public User updateUser(@Valid @RequestBody User user);
    public List<User> getUsers();
    public User getUserForId(long id);
}
