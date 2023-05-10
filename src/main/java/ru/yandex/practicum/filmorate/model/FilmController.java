package ru.yandex.practicum.filmorate.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {
    int id = 0;
    HashMap<Integer, Film> films = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        LocalDate releaseDate = LocalDate.of(1895, 12, 28);
        int maxDescription = 200;
        if (film.getName().isEmpty()) {
            logger.error("Неверное название фильма: {}", film.getName());
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDuration() <= 0) {
            logger.error("Неверная продолжительность фильма:{}", film.getDuration());
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(releaseDate)) {
            logger.error("Неверная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDescription().length() > maxDescription) {
            logger.error(" Не верное описание: {}", film.getDescription());
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        id++;
        film.setId(id);
        films.put(id, film);
        logger.info("Добавлен новый фильм: {}", film.getName());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            logger.info("Фильм обновлен: {}", film.getName());
        } else {
            logger.error("Фильм не найден: {}", film.getName());
            throw new ValidationException("Фильм не найден");
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        logger.debug("Получен список всех фильмов");
        return new ArrayList<>(films.values());
    }
}
