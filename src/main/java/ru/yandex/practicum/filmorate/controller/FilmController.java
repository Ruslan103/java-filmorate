package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    FilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(inMemoryFilmStorage);

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    @GetMapping("/films/{id}")
    public ResponseEntity getFilmForId(@PathVariable int id) {
        Film film = inMemoryFilmStorage.getFilmForId(id);
        if (film == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable long userId) {
        filmService.addLikedFilmUser(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity deleteLikeFilmUser(@PathVariable int id, @PathVariable long userId) {
        Film film = inMemoryFilmStorage.getFilmForId(id);
        if (film == null || !film.getLikedFilmUsers().contains(userId)) {
            return ResponseEntity.notFound().build();
        }
        filmService.deleteLikedFilmUser(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getLikedFilmUser(count);
    }
}
