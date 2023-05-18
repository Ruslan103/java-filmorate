package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
}
