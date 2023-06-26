package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id;
    private String genre;

    public Genre(int id, String genre) {
        this.id = id;
        this.genre = genre;
    }

    public Genre(int id) {
        this.id = id;
    }

}
