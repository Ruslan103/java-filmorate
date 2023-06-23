package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

//    public User(long id, String email, String login, String name, LocalDate birthday) {
//        this.id = id;
//        this.email = email;
//        this.login = login;
//        this.name = name;
//        this.birthday = birthday;
//    }

    @JsonIgnore
    private Set<Long> friends = new HashSet<>();

    public String getName() {
        if (name == null) {
            return login;
        }
        return name;
    }
}
