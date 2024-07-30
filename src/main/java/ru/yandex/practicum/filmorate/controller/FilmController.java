package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
        this.filmStorage = filmService.getFilmStorage();
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmStorage.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        filmService.checkFilm(film);
        return filmStorage.addFilm(film);
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

    @GetMapping("/popular?count={count}")
    public List<Film> getPopular(@PathVariable Integer count) {
        if (count == null)
            count = 10;
        return filmService.getPopularFilms(count);
    }

    @PutMapping
    public Film editFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Ошибка! Идентификатор фильма не задан");
            throw new ValidationException("Ошибка! Идентификатор фильма не задан");
        }

        if (filmStorage.containsFilmById(film.getId())) {
            filmService.checkFilm(film);
            log.debug("Изменение параметров фильма с идентификатором {}", film.getId());
            return filmStorage.editFilm(film);
        } else {
            log.error("Ошибка! Фильма с заданным идентификатором не существует");
            throw new ValidationException("Ошибка! Фильма с заданным идентификатором не существует");
        }
    }
}
