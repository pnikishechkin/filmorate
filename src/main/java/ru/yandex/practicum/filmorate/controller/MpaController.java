package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

/**
 * Контроллер для реализации API методов, связанных с рейтингами фильмов
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MpaController {

    private final MpaService mpaService;

    /**
     * Получить список всех рейтингов фильмов
     *
     * @return список возможных рейтингов
     */
    @GetMapping
    public List<Mpa> getRatings() {
        return mpaService.getMpa();
    }

    /**
     * Получить рейтинг фильма по идентификатору
     *
     * @param id идентификатор рейтинга
     * @return объект рейтинга (опционально)
     */
    @GetMapping("/{id}")
    public Mpa getRating(@PathVariable @Positive Integer id) {
        return mpaService.getRatingById(id);
    }

    /**
     * Добавить рейтинг
     *
     * @param rating объект рейтинга
     * @return новый объект рейтинга
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa addRating(@RequestBody Mpa rating) {
        return mpaService.addRating(rating);
    }
}