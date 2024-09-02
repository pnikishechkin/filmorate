package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Связь фильм-жанр
 */
@Data
@Builder
@EqualsAndHashCode
@ToString
public class FilmGenre {
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer genreId;
}
