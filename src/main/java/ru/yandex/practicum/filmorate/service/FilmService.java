package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;

    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public void addUserLike(Integer filmId, Integer userId) {
        if (filmStorage.containsFilmById(filmId)) {
            filmStorage.getFilmById(filmId).getUsersLike().add(userId);
        }
    }

    public void deleteUserLike(Integer filmId, Integer userId) {
        if (filmStorage.containsFilmById(filmId)) {
            filmStorage.getFilmById(filmId).getUsersLike().remove(userId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream().sorted(Comparator.comparingInt(f -> f.getUsersLike().size()))
                .limit(count).collect(Collectors.toList());
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
}
