package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    private Set<User> friends;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Film> likeFilms;
}
