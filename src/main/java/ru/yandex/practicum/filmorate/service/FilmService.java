package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public void addLikedFilmUser(int filmId, long userId) {
        Film film = inMemoryFilmStorage.getFilmForId(filmId);
        film.getLikedFilmUsers().add(userId);
    }

    public void deleteLikedFilmUser(int filmId, long userId) {
        Film film = inMemoryFilmStorage.getFilmForId(filmId);
        film.getLikedFilmUsers().remove(userId);
    }

    public List<Film> getLikedFilmUser(int count) {
        List<Film> films = inMemoryFilmStorage.getFilms();
        films.sort(Comparator.comparingInt(film -> film.getLikedFilmUsers().size()));
        Collections.reverse(films);
        List<Film> topFilms = films.stream().limit(count).collect(Collectors.toList());
        return topFilms;
    }
}
