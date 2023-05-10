package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmController;
import ru.yandex.practicum.filmorate.model.ValidationException;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
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
}
