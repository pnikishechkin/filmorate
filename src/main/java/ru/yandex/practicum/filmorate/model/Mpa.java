package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Рейтинг фильма
 */
@Data
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Mpa {
    @NotNull
    private Integer id;
    @NotNull
    private String name;
}
