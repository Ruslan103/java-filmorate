package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserStorage inMemoryUserStorage;
    @Autowired
    private UserService userService;

//    @BeforeEach
//    public void setUp() {
//        //userController = new UserController();
//        userService=new UserService();
//        userController.setInMemoryUserStorage(inMemoryUserStorage);
//        userController.setUserService(userService);
//    }

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
        inMemoryUserStorage = new InMemoryUserStorage();
        userController.setInMemoryUserStorage(inMemoryUserStorage);
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
    public void testAddFriend() {
        userController.setInMemoryUserStorage(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userService.addFriend(user1.getId(), user2.getId());
        Set<Long> userResult = user1.getFriends();
        Set<Long> friendResult = user2.getFriends();
        assertTrue(userResult.contains(user2.getId()));
        assertTrue(friendResult.contains(user1.getId()));
    }

    @Test
    public void testAddFriendWithIncorrectId() {
        userController.setInMemoryUserStorage(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
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
        userController.setInMemoryUserStorage(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(user1.getId(), user2.getId());
        Set<Long> userResult = user1.getFriends();
        Set<Long> friendResult = user2.getFriends();
        assertTrue(userResult.contains(user2.getId())); // проверяем что добавление в друзья прошло успешно
        assertTrue(friendResult.contains(user1.getId()));
        userController.deleteFriend(user1.getId(), user2.getId());
        assertFalse(userResult.contains(user2.getId()));
        assertFalse(friendResult.contains(user1.getId()));
    }

    @Test
    public void testDeleteFriendWithIncorrectId() {
        userController.setInMemoryUserStorage(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);
        userController.addFriend(user1.getId(), user2.getId());
        Set<Long> userResult = user1.getFriends();
        Set<Long> friendResult = user2.getFriends();
        assertTrue(userResult.contains(user2.getId())); // проверяем что добавление в друзья прошло успешно
        assertTrue(friendResult.contains(user1.getId()));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteFriend(9999, user2.getId());
        });
        assertEquals("Пользователь не найден", exception.getMessage());
        UserNotFoundException exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteFriend(user1.getId(), 9999);
        });
        assertEquals("Пользователь не найден", exception2.getMessage());
    }

    @Test
    public void testGetFriends() {
        userController.setInMemoryUserStorage(inMemoryUserStorage);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        User user3 = new User("testLogin2", "Test User3", "test3@test.com", LocalDate.of(1990, 1, 1));
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


}
