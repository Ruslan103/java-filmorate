package ru.yandex.practicum.filmorate.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
@Data
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private static final int MAX_DESCRIPTION = 200;
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;

    @Override
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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration,mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        List<Genre> genres = film.getGenres();
        String sqlGenre = "INSERT INTO film_genre (film_id,genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlGenre);
                    stmt.setInt(1, film.getId());
                    stmt.setInt(2, genre.getId());
                    return stmt;
                });
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return getFilmForId(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Film film = new Film(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration")
            );
            film.setId(rs.getInt("film_id"));
            return film;
        });
    }

    @Override
    public Film getFilmForId(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);
        if (filmRows.next()) {
            int mpaId = filmRows.getInt("mpa_id");
            Mpa mpa = getMpaForId(mpaId);
            Film film = new Film(
                    Objects.requireNonNull(filmRows.getString("name")),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration")
            );
            List <Genre> genres =getGenresById(id);
            film.setGenres(genres);
            film.setMpa(mpa);
            film.setId(id);
            return film;
        } else {
            log.error("Фильм по id не найден");
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public Genre getGenreForId(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")
            );
            return genre;
        } else {
            log.error("Жанр по id не найден");
            throw new FilmNotFoundException("Жанр не найден");
        }
    }

    public List<Genre> getGenresById(Integer filmId) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from film_genre where film_id = ?", filmId);
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = getGenreForId(genreRows.getInt("genre_id"));
            genres.add(genre);
        }
        return genres;
    }

    public Mpa getMpaForId(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa where mpa_id= ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("mpa_id"),
                    mpaRows.getString("name"));
            return mpa;
        } else {
            log.error("MPA по id не найден");
            throw new FilmNotFoundException("MPA не найден");
        }
    }
}
