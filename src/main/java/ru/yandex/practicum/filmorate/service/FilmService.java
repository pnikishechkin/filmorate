package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
    }

    public Film addFilm(Film film) {
        this.checkFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film editFilm(Film film) {
        log.debug("Изменение параметров фильма с идентификатором {}", film.getId());
        Film res = filmStorage.editFilm(film).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным " +
                "идентификатором не существует"));
        checkFilm(res);
        return res;
    }

    public void addUserLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с " +
                "заданным идентификатором не существует"));
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
        film.getUsersIdLike().add(userId);
    }

    public void deleteUserLike(Integer filmId, Integer userId) {

        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с " +
                "заданным идентификатором не существует"));
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
        film.getUsersIdLike().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream().sorted(Comparator.comparingInt(f -> f.getUsersIdLike().size()))
                .limit(count).collect(Collectors.toList()).reversed();
    }

    private void checkFilm(Film film) {
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
