package ru.yandex.practicum.filmorate.storage.film;

public class FilmNotFoundException extends RuntimeException {
    String parameter;

    public FilmNotFoundException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
