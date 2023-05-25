package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    String parameter;

    public FilmNotFoundException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
