package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * Контроллер для реализации API методов, связанных с жанрами фильмов
 */
@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
@Validated
public class GenreController {

    private final GenreService genreService;

    /**
     * Получить список всех жанров фильмов
     *
     * @return список жанров
     */
    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    /**
     * Получить жанр по идентификатору
     *
     * @param id идентификатор жанра
     * @return объект жанра
     */
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable @Positive Integer id) {
        return genreService.getGenreById(id);
    }
}