package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class FilmService {
    @Autowired
    private FilmStorage inMemoryFilmStorage;

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
