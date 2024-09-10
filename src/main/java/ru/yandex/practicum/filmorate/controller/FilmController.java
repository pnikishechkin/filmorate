package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Контроллер для реализации API методов, связанных с фильмами
 */
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @DeleteMapping
    public Boolean deleteFilm(@RequestBody Film film) {
        return filmService.deleteFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        filmService.addUserLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        filmService.deleteUserLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") Integer count,
                                       @RequestParam(value = "id", defaultValue = "1") Integer id,
                                       @RequestParam(value = "releaseDate", defaultValue = "1999") LocalDate releaseDate) {
        return filmService.getPopularFilms(count, id, releaseDate);
    }

}
