package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    public Film getFilmForId(@PathVariable int id) {
        return inMemoryFilmStorage.getFilmForId(id);
    }

    @PutMapping("/films/{id}/like/{userId}") //  проверить что добавляется только один пользователь. переделать на триСет
    public void likeFilm(@PathVariable int id, @PathVariable long userId) {
        filmService.addLikedFilmUser(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeFilmUser(@PathVariable int id, @PathVariable long userId) {
        filmService.deleteLikedFilmUser(id, userId);
    }

    @GetMapping("films/popular?count={count}")
    public List <Film> getTenLikedFilmUser (int count){
       return filmService.getTenLikedFilmUser(count);
    }

}
