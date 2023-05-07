package ru.yandex.practicum.filmorate.model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();
    int id = 0;

    @PostMapping("/films/add")
    public void addFilm(@RequestBody Film film) {
        LocalDate releaseDate = LocalDate.of(1895, 12, 28);
        int MAX_DESCRIPTION = 200;
        if (film.getName().isEmpty()) {
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDuration() <=0) {
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(releaseDate)) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        id++;
        film.setId(id);
        films.put(id, film);
    }

    @PostMapping("/films/update")
    public void updateFilm(@RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
        }
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
