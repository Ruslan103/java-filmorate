package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Data
public class FilmController {
    private FilmService filmService;

    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmForId(@PathVariable int id) {
        return filmStorage.getFilmForId(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable int id, @PathVariable long userId) {
        filmService.addLikedFilmUser(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLikeFilmUser(@PathVariable int id, @PathVariable long userId) {
        Film film = filmStorage.getFilmForId(id);
        filmService.deleteLikedFilmUser(id, userId);
        return film;
    }

    @GetMapping("films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getLikedFilmUser(count);
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaForId(@PathVariable int id) {
        return filmStorage.getMpaForId(id);
    }

    @GetMapping("/genres/{id}")
    Genre getGenreForId(@PathVariable int id) {
        return filmStorage.getGenreForId(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }
}
