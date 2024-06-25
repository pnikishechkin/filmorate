package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        this.checkFilm(film);
        film.setId(getNewId());
        log.debug("Добавление нового фильма с идентификатором {}", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film editFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Ошибка! Идентификатор фильма не задан");
            throw new ValidationException("Ошибка! Идентификатор фильма не задан");
        }
        if (films.containsKey(film.getId())) {
            checkFilm(film);
            log.debug("Изменение параметров фильма с идентификатором {}", film.getId());
            Film oldFilm = films.get(film.getId());
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setDuration(film.getDuration());
            oldFilm.setReleaseDate(film.getReleaseDate());
            return oldFilm;
        }
        log.error("Ошибка! Фильма с заданным идентификатором не существует");
        throw new ValidationException("Ошибка! Фильма с заданным идентификатором не существует");
    }

    public void checkFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Ошибка! Название фильма не может быть пустым");
            throw new ValidationException("Ошибка! Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка! Максимальная длина описания фильма — 200 символов");
            throw new ValidationException("Ошибка! Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка! Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Ошибка! Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Ошибка! Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Ошибка! Продолжительность фильма должна быть положительным числом");
        }
    }

    public int getNewId() {
        int maxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }
}
