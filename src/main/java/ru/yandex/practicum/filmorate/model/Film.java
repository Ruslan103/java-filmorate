package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private String genre;
    private String rating;
    @JsonIgnore
    private Set<Long> likedFilmUsers = new HashSet<>();

    public Film(@NonNull String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
