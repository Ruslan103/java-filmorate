package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    private final FilmController filmController;
    @Autowired
    private final FilmDbStorage filmDbStorage;
    @Autowired
    private final FilmService filmService;
    @Autowired
    private final UserDbStorage userDbStorage;
    private final Mpa mpa = new Mpa(1, "G");

    @Test
    public void testAddFilm() {
        Film film = new Film("Название", "Описание", LocalDate.now(), 90, mpa);
        Film result = filmController.addFilm(film);
        assertEquals(film, result);
    }

    @Test
    public void testAddFilmWithInvalidName() {
        Film film = new Film("", "Описание", LocalDate.now(), 90);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("название не может быть пустым", exception.getMessage());
    }

    @Test
    public void testAddFilmWithInvalidDuration() {
        Film film = new Film("Название", "Описание", LocalDate.now(), -1);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() {
        Film film = new Film("Название", "Описание", LocalDate.of(1895, 12, 27), 90);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void testAddFilmWithInvalidDescription() {
        Film film = new Film("Название", "a".repeat(201), LocalDate.now(), 90);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film("Название", "Описание", LocalDate.now(), 90, mpa);
        Film addedFilm = filmController.addFilm(film);
        Film updatedFilm = new Film("Новое название", "Новое описание", addedFilm.getReleaseDate(), addedFilm.getDuration(), mpa);
        updatedFilm.setId(addedFilm.getId());
        Film result = filmController.updateFilm(updatedFilm);
        assertEquals(updatedFilm, result);
    }

    @Test
    public void testUpdateFilmWithInvalidId() {
        Film film = new Film("Название", "Описание", LocalDate.now(), 90, mpa);
        Film updatedFilm = new Film("Новое название", "Новое описание", film.getReleaseDate(), film.getDuration(), mpa);
        updatedFilm.setId(film.getId());
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.updateFilm(updatedFilm);
        });
    }

    @Test
    public void testGetFilms() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        Film film2 = new Film("Название2", "Описание2", LocalDate.now().plusDays(1), 120, mpa);
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        List<Film> result = filmController.getFilms();
        assertEquals(2, result.size());
        assertEquals(film1, result.get(0));
        assertEquals(film2, result.get(1));
    }

    @Test
    public void testAddLikedFilmUser() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user1);
        filmController.addFilm(film1);
        filmController.addLikeFilm(1, 1);
        assertEquals(filmService.getLikedFilmUser(10).size(), 1);
        assertTrue(filmService.getLikedFilmUser(1).contains(film1));
    }

    @Test
    public void testAddLikedFilmUserWithIncorrectId() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user1);
        filmController.addFilm(film1);
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.addLikeFilm(9999, user1.getId());
        });
        assertEquals(exception.getParameter(), "Фильм не найден");
    }

    @Test
    public void testDeleteLikedFilmUser() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user1);
        User user2 = new User("testLogin2@test.com", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user2);
        filmController.addFilm(film1);
        filmController.addLikeFilm(1, 1);
        filmController.addLikeFilm(1, 2);
        filmController.deleteLikeFilmUser(1, 2);
        assertFalse(filmService.getLikedFilmUser(10).contains(user1));
    }

    @Test
    public void testDeleteLikedFilmUserWithIncorrectId() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user1);
        User user2 = new User("testLogin2@test.com", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user2);
        filmController.addFilm(film1);
        filmController.addLikeFilm(1, 1);
        filmController.addLikeFilm(1, 2);
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.deleteLikeFilmUser(9999, 1);
        });
        assertEquals(exception.getParameter(), "Фильм не найден");
    }

    @Test
    public void testGetLikedFilmUser() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90, mpa);
        Film film2 = new Film("Название2", "Описание2", LocalDate.now(), 90, mpa);
        Film film3 = new Film("Название3", "Описание3", LocalDate.now(), 90, mpa);
        Film film4 = new Film("Название4", "Описание4", LocalDate.now(), 90, mpa);
        Film film5 = new Film("Название5", "Описание5", LocalDate.now(), 90, mpa);
        User user1 = new User("testLogin1@test.com", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        User user2 = new User("testLogin2@test.com", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        User user3 = new User("testLogin3@test.com", "Test User3", "test3@test.com", LocalDate.of(1990, 1, 1));
        User user4 = new User("testLogin4@test.com", "Test User4", "test4@test.com", LocalDate.of(1990, 1, 1));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        userDbStorage.addUser(user4);
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        filmController.addFilm(film3);
        filmController.addFilm(film4);
        filmController.addLikeFilm(1, 1);
        filmController.addLikeFilm(1, 2);
        filmController.addLikeFilm(1, 3);
        filmController.addLikeFilm(2, 1);
        filmController.addLikeFilm(2, 2);
        filmController.addLikeFilm(2, 3);
        filmController.addLikeFilm(2, 4);
        filmController.addLikeFilm(3, 1);
        List<Film> result = filmController.getPopularFilms(3);
        assertEquals(result.size(), 3);
        assertTrue(result.contains(film1));
        assertTrue(result.contains(film2));
        assertTrue(result.contains(film3));
        assertFalse(result.contains(film4));
    }
}
