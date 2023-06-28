package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmForId(int id) throws FilmNotFoundException;

    Mpa getMpaForId(int id);

    Set<Genre> getGenresByFilmId(Integer filmId);

    Genre getGenreForId(int id);

    List<Genre> getAllGenres();

    List<Mpa> getAllMpa();
}
