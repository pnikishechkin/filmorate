package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Integer id;
    private Rating rating;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Genre> genres;
}
