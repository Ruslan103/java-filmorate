package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
public class FilmService {
    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    public void addLikedFilmUser(int filmId, long userId) {
        Film film = filmStorage.getFilmForId(filmId);
        if (film == null) {
            log.error("Неверно указан id фильма");
            throw new FilmNotFoundException("Фильм не найден");
        }
        String sql = "INSERT INTO film_user_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setLong(1, filmId);
            stmt.setLong(2, userId);
            return stmt;
        });
    }

    public void deleteLikedFilmUser(int filmId, long userId) {
        Film film = filmStorage.getFilmForId(filmId);
        if (film == null || userId<=0) {
            log.error("Неверно указан id фильма либо пользователя");
            throw new FilmNotFoundException("Фильм не найден");
        }
        String sqlQuery = "delete from film_user_like where film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public List<Film> getLikedFilmUser(int count) {
        String sql = "SELECT films.*, COUNT(*) as likes_count " +
                "FROM film_user_like " +
                "JOIN films on films.film_id = film_user_like.film_id " +
                "GROUP BY films.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT "+count;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = filmStorage.getFilmForId(rs.getInt("film_id"));
            return film;
        });
    }
}
