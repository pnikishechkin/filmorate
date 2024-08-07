package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Integer id;
    private String name;
    private Set<Integer> usersIdLike;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
