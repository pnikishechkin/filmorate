package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Контроллер для реализации API методов, связанных с фильмами
 */
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmService filmService;

    /**
     * Получить все фильмы
     *
     * @return список всех фильмов
     */
    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    /**
     * Получить фильм по идентификатору
     *
     * @param id идентификатор фильма
     * @return опционально - объект фильма
     */
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive final Integer id) {
        return filmService.getFilmById(id);
    }

    /**
     * Получить множество фильмов, которые лайкнул указанный пользователь
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     * @return множество фильмов
     */
    @GetMapping("/common")
    public Set<Film> getCommonFilms(@RequestParam final Integer userId, @RequestParam final Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    /**
     * Добавить новый фильм
     *
     * @param film объект добавляемого фильма
     * @return объект добавленного фильма
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    /**
     * Удалить фильм
     *
     * @param id идентификатор удаляемого фильма
     * @return флаг, был ли удален фильм
     */
    @DeleteMapping("/{id}")
    public Boolean deleteFilm(@PathVariable @Positive final Integer id) {
        return filmService.deleteFilm(id);
    }

    /**
     * Редактирование фильма
     *
     * @param film объект изменяемого фильма
     * @return объект измененного фильма
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Добавить лайк от выбранного пользователя указанному фильму
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable final Integer id,
                        @PathVariable final Integer userId) {
        filmService.addUserLike(id, userId);
    }

    /**
     * Удалить лайк выбранного пользователя указанному фильму
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final Integer id,
                           @PathVariable final Integer userId) {
        filmService.deleteUserLike(id, userId);
    }

    /**
     * Получить список популярных фильмов
     *
     * @param count   количество выводимых фильмов
     * @param genreId жанр выводимых фильмов
     * @param year    год выводимых фильмов
     * @return список фильмов
     */
    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") Integer count,
                                       Integer genreId,
                                       Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    /**
     * Получить список фильмов по идентификатору режиссера
     *
     * @param directorId идентификатор режиссера
     * @param sortBy     вариант сортировки
     * @return список фильмов
     */
    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable @Positive final Integer directorId, @RequestParam @NotBlank String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    /**
     * Получить список фильмов по поиску
     *
     * @param query текст для поиска
     * @param by    принимает значения director (поиск по режиссёру), title (поиск по названию), либо оба значения враз
     * @return список фильмов
     */
    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam @NotBlank final String query, @RequestParam(required = false, defaultValue = "title") final String by) {
        return filmService.searchFilm(query, by);
    }
}
