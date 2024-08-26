package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Film {
    private Integer id;
    private Mpa mpa;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private LinkedHashSet<Genre> genres;
}
