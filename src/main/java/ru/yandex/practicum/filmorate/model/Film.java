package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
    private List<Genre> genres;
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

    public Film(int id, @NonNull String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film() {
    }

    public Film(int id, @NonNull String name, String description, LocalDate releaseDate, int duration, List<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }
}
