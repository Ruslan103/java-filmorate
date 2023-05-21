package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    public void addUserTest() {
        User user = new User("testLogin", "Test User", "test@test.com", LocalDate.of(1990, 1, 1));
        User result = userController.addUser(user);
        assertEquals(user, result);
    }

    @Test
    public void testAddUserWithEmptyEmail() {
        User user = new User("testLogin", "", "Test User", LocalDate.of(1990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testAddUserWithEmptyLogin() {
        User user = new User("", "Test User", "test@test.com", LocalDate.of(1990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testAddUserWithInvalidBBirthday() {
        User user = new User("testLogin", "Test User", "test@test.com", LocalDate.of(5990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        User user = new User("testLogin", "Test User", "test@test.com", LocalDate.of(1990, 1, 1));
        User result = userController.addUser(user);
        User updateUser = new User("newTestLogin", "Test User", "newTest@test.com", LocalDate.of(1990, 1, 1));
        updateUser.setId(result.getId());
        User newUser = userController.updateUser(updateUser);
        assertEquals(updateUser, newUser);
    }

    @Test
    public void testUpdateNonExistentUser() {
        User user = new User("testLogin", "Test User", "test@test.com", LocalDate.of(1990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testGetUser() {
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        List<User> result = userController.getUsers();
        assertEquals(result.size(), 2);
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    public void testAddUser() {
        UserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        inMemoryUserStorage.addUser(user1);
        inMemoryUserStorage.addUser(user2);
        userService.addFriend(user1.getId(), user2.getId());
        assertTrue(user1.getFriends().contains(user2.getId()));

    }

}
