package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final int MAX_DESCRIPTION = 200;
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    public Film addFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Неверное название фильма: {}", film.getName());
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDuration() <= 0) {
            log.error("Неверная продолжительность фильма:{}", film.getDuration());
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            log.error("Неверная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION) {
            log.error(" Не верное описание: {}", film.getDescription());
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        id++;
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен новый фильм: {}", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("Фильм обновлен: {}", film.getName());
        } else {
            log.error("Фильм не найден: {}", film.getName());
            throw new ValidationException("Фильм не найден");
        }
        return film;
    }

    public List<Film> getFilms() {
        log.debug("Получен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    public Film getFilmForId(int id) {
        Film film = films.get(id);
        if (film == null) {
            log.error("Фильм по id не найден");
            throw new FilmNotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public Mpa getMpaForId(int id) {
        return null;
    }

    @Override
    public Set<Genre> getGenresByFilmId(Integer filmId) {
        return null;
    }

    @Override
    public Genre getGenreForId(int id) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return null;
    }
}
