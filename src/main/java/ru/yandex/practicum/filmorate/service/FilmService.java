package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
public class FilmService {
    @Autowired
    private FilmStorage inMemoryFilmStorage;

    public void addLikedFilmUser(int filmId, long userId) {
        Film film = inMemoryFilmStorage.getFilmForId(filmId);
        film.getLikedFilmUsers().add(userId);
    }

    public void deleteLikedFilmUser(int filmId, long userId) {
        Film film = inMemoryFilmStorage.getFilmForId(filmId);
        if (film == null || !film.getLikedFilmUsers().contains(userId)) {
            log.error("Неверно указан id фильма либо пользователя");
            throw new FilmNotFoundException("Фильм не найден");
        }
        film.getLikedFilmUsers().remove(userId);
    }

    public List<Film> getLikedFilmUser(int count) {
        List<Film> films = inMemoryFilmStorage.getFilms();
        films.sort(Comparator.comparingInt(film -> film.getLikedFilmUsers().size()));
        Collections.reverse(films);
        return films.stream().limit(count).collect(Collectors.toList());
    }
}
