package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private final FilmController filmController;

    @Autowired
    public FilmControllerTest(FilmController filmController) {
        this.filmController = filmController;
    }


    @Test
    public void testAddFilm() {
        Film film = new Film("Название", "Описание", LocalDate.now(), 90);
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
        Film film = new Film("Название", "Описание", LocalDate.now(), 90);
        Film addedFilm = filmController.addFilm(film);
        Film updatedFilm = new Film("Новое название", "Новое описание", addedFilm.getReleaseDate(), addedFilm.getDuration());
        updatedFilm.setId(addedFilm.getId());
        Film result = filmController.updateFilm(updatedFilm);
        assertEquals(updatedFilm, result);
    }

    @Test
    public void testUpdateFilmWithInvalidId() {
        Film film = new Film("Название", "Описание", LocalDate.now(), 90);
        Film updatedFilm = new Film("Новое название", "Новое описание", film.getReleaseDate(), film.getDuration());
        updatedFilm.setId(film.getId());
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(updatedFilm);
        });
        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    public void testGetFilms() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90);
        Film film2 = new Film("Название2", "Описание2", LocalDate.now().plusDays(1), 120);
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        List<Film> result = filmController.getFilms();
        assertEquals(2, result.size());
        assertEquals(film1, result.get(0));
        assertEquals(film2, result.get(1));
    }

    @Test
    public void testAddLikedFilmUser() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        user1.setId(1);
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        user2.setId(2);
        filmController.addFilm(film1);
        filmController.addLikeFilm(film1.getId(), user1.getId());
        filmController.addLikeFilm(film1.getId(), user2.getId());
        assertEquals(film1.getLikedFilmUsers().size(), 2);
        assertTrue(film1.getLikedFilmUsers().contains(user1.getId()));
        assertTrue(film1.getLikedFilmUsers().contains(user2.getId()));
    }

    @Test
    public void testAddLikedFilmUserWithIncorrectId() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        user1.setId(1);
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        user2.setId(2);
        filmController.addFilm(film1);
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.addLikeFilm(9999, user1.getId());
        });
        assertEquals(exception.getParameter(), "Фильм не найден");
    }

    @Test
    public void testDeleteLikedFilmUser() {
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        user1.setId(1);
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        user2.setId(2);
        filmController.addFilm(film1);
        filmController.addLikeFilm(film1.getId(), user1.getId());
        filmController.addLikeFilm(film1.getId(), user2.getId());
        assertTrue(film1.getLikedFilmUsers().contains(user1.getId())); //проверяем что лайки добавлены
        assertTrue(film1.getLikedFilmUsers().contains(user2.getId()));
        filmController.deleteLikeFilmUser(film1.getId(), user2.getId());
        assertFalse(film1.getLikedFilmUsers().contains(user2.getId()));
        assertTrue(film1.getLikedFilmUsers().contains(user1.getId()));
    }
    @Test
    public void testDeleteLikedFilmUserWithIncorrectId(){
        Film film1 = new Film("Название1", "Описание1", LocalDate.now(), 90);
        User user1 = new User("testLogin1", "Test User1", "test1@test.com", LocalDate.of(1990, 1, 1));
        user1.setId(1);
        User user2 = new User("testLogin2", "Test User2", "test2@test.com", LocalDate.of(1990, 1, 1));
        user2.setId(2);
        filmController.addFilm(film1);
        filmController.addLikeFilm(film1.getId(), user1.getId());
        filmController.addLikeFilm(film1.getId(), user2.getId());
        assertTrue(film1.getLikedFilmUsers().contains(user1.getId())); //проверяем что лайки добавлены
        assertTrue(film1.getLikedFilmUsers().contains(user2.getId()));
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.deleteLikeFilmUser(9999, user1.getId());
        });
        assertEquals(exception.getParameter(), "Фильм не найден");
    }



}
