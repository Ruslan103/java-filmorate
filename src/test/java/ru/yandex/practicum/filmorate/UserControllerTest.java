package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private UserService userService;

    @Test
    public void addUserTest() {
        User user = new User("test@test.com", "Test User", "test", LocalDate.of(1990, 1, 1));
        User result = userController.addUser(user);
        assertEquals(user, result);
    }

    @Test
    public void testAddUserWithEmptyEmail() {
        User user = new User(" ", "testLogin", "Test User", LocalDate.of(1990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testAddUserWithEmptyLogin() {
        User user = new User("user@test.com", "", "test", LocalDate.of(1990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testAddUserWithInvalidBBirthday() {
        User user = new User("test@test.com", "Test User", "test", LocalDate.of(5990, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        User user = new User("test@test.com", "Test User", "test", LocalDate.of(1990, 1, 1));
        User result = userController.addUser(user);
        User updateUser = new User("newTestLogin", "Test User", "newTest@test.com", LocalDate.of(1990, 1, 1));
        updateUser.setId(result.getId());
        User newUser = userController.updateUser(updateUser);
        assertEquals(updateUser, newUser);
    }

    @Test
    public void testUpdateNonExistentUser() {
        User user = new User("test@test.com", "Test User", "test", LocalDate.of(1990, 1, 1));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userController.updateUser(user);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testGetUser() {
        // userStorage = new InMemoryUserStorage();
        userController.setUserStorage(userStorage);
        User user1 = new User("test1@test", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("test2@test.com", "Test User2", "test", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        List<User> result = userController.getUsers();
        assertEquals(result.size(), 2);
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    public void testAddFriend() {
        userController.setUserStorage(userStorage);
        User user1 = new User("test1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("test2@test.com", "Test User2", "test2", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userService.addFriend(1, 2);
        List<User> userResult = userController.getFriends(1);
        List<Long> usersId = new ArrayList<>();
        assertTrue(userResult.contains(userController.getUserForId(2)));
    }

    @Test

    public void testAddFriendWithIncorrectId() {
        userController.setUserStorage(userStorage);
        User user1 = new User("test1@test.com", "Test User1", "test", LocalDate.of(1990, 1, 1));
        User user2 = new User("test@test.com", "Test User2", "test", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.addFriend(9999, user2.getId());
        });
        assertEquals("Пользователь не найден", exception.getMessage());
        UserNotFoundException exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.addFriend(user1.getId(), 9999);
        });
        assertEquals("Пользователь не найден", exception2.getMessage());
    }

    @Test
    public void testDeleteFriend() {
        userController.setUserStorage(userStorage);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2@test.com", "Test User2", "test2", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(user1.getId(), user2.getId());
        List<User> userResult = userController.getFriends(1);
        assertTrue(userResult.contains(userController.getUserForId(2))); // проверяем что добавление в друзья прошло успешно
        userController.deleteFriend(1, 2);
        List<User> userResult2 = userController.getFriends(1);
        assertFalse(userResult2.contains(userController.getUserForId(2)));
    }

    @Test
    public void testDeleteFriendWithIncorrectId() {
        userController.setUserStorage(userStorage);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2@test.com", "Test User2", "test2", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(1, 2);
        List<User> userResult = userController.getFriends(1);
        assertTrue(userResult.contains(userController.getUserForId(2))); // проверяем что добавление в друзья прошло успешно
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteFriend(-2, 2);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
        UserNotFoundException exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteFriend(1, -2);
        });
        assertEquals("Пользователь не найден", exception2.getMessage());
    }

    @Test
    public void testGetFriends() {
        userController.setUserStorage(userStorage);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2@test.com", "Test User2", "test2", LocalDate.of(1990, 1, 1));
        User user3 = new User("testLogin3@test.com", "Test User3", "test3", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);
        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), user3.getId());
        List<User> userResult = userController.getFriends(user1.getId());
        assertEquals(userResult.size(), 2);
        assertTrue(userResult.contains(user2));
        assertTrue(userResult.contains(user3));
    }

    @Test
    public void testMutualFriends() {
        User user1 = new User("testLogin1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2@test.com", "Test User2", "test2", LocalDate.of(1990, 1, 1));
        User user3 = new User("testLogin3@test.com", "Test User3", "test3", LocalDate.of(1990, 1, 1));
        User user4 = new User("testLogin4@test.com", "Test User4", "test4", LocalDate.of(1990, 1, 1));
        User user5 = new User("testLogin5@test.com", "Test User5", "test5", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);
        userController.addUser(user4);
        userController.addUser(user5);
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(4, 2);
        userController.addFriend(4, 3);
        userController.addFriend(4, 5);
        List<User> mutualFriends = userController.mutualFriends(user1.getId(), user4.getId());
        assertEquals(mutualFriends.size(), 2);
        assertTrue(mutualFriends.contains(user2));
        assertTrue(mutualFriends.contains(user3));
        assertFalse(mutualFriends.contains(user5));
    }
}
