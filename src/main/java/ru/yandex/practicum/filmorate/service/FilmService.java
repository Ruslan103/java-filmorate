package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public List<Film> getTenLikedFilmUser(int count) {
        List<Film> films = inMemoryFilmStorage.getFilms(); // список фильмов
        films.sort(Comparator.comparingInt(film -> film.getLikedFilmUsers().size()));
        Collections.reverse(films);
        List<Film> topFilms = films.subList(0, Math.min(films.size(), 10));
        return topFilms;
    }
}
