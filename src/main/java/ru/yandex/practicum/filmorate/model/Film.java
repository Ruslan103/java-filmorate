package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @JsonProperty("genres")
    private Set<Genre> genres;
    @JsonProperty("mpa")
    private Mpa mpa;

    @JsonIgnore
    private Set<Long> likedFilmUsers = new HashSet<>();

    public Film(@NonNull String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(@NonNull String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film() {
    }

    public Film(int id, @NonNull String name, String description, LocalDate releaseDate, int duration, Set<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        if (((Film) o).genres == null) {
            ((Film) o).genres = Collections.emptySet();
        }
        if (film.genres == null) {
            film.genres = Collections.emptySet();
        }
        if (genres == null) {
            genres = Collections.emptySet();
        }
        return id == film.id && duration == film.duration && name.equals(film.name) && description.equals(film.description)
                && releaseDate.equals(film.releaseDate) && genres.equals(film.genres) && mpa.equals(film.mpa)
                && likedFilmUsers.equals(film.likedFilmUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, genres, mpa, likedFilmUsers);
    }
}
