package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;
import ru.yandex.practicum.filmorate.repository.rating.RatingDbRepository;
import ru.yandex.practicum.filmorate.repository.user.UserDbRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbRepository filmDbRepository;
    private final RatingDbRepository ratingDbRepository;
    private final GenreDbRepository genreDbRepository;
    private final UserDbRepository userDbRepository;

    public List<Film> getFilms() {
        return filmDbRepository.getAll();
    }

    public Film getFilmById(Integer id) {
        return filmDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным " +
                "идентификатором не существует"));
    }

    public Film addFilm(Film film) {
        if (ratingDbRepository.getById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Ошибка! Рейтинга с заданным идентификатором не существует");
        }
        this.checkFilm(film);
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                if (genreDbRepository.getById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Ошибка! Жанра с заданным идентификатором не существует");
                }
            });
        }
        return filmDbRepository.addFilm(film);
    }

    public Boolean deleteFilm(Film film) {
        if (filmDbRepository.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует");
        }
        return filmDbRepository.deleteFilm(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Изменение параметров фильма с идентификатором {}", film.getId());

        if (filmDbRepository.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует");
        }
        if (ratingDbRepository.getById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Ошибка! Рейтинга с заданным идентификатором не существует");
        }
        this.checkFilm(film);
        return filmDbRepository.updateFilm(film);
    }

    public void addUserLike(Integer filmId, Integer userId) {
        if (filmDbRepository.getById(filmId).isEmpty()) {
            throw new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует");
        }
        if (userDbRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        filmDbRepository.adduserLike(filmId, userId);
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

    public void deleteUserLike(Integer filmId, Integer userId) {
        if (filmDbRepository.getById(filmId).isEmpty()) {
            throw new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует");
        }
        if (userDbRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        filmDbRepository.deleteUserLike(filmId, userId);
    }

    public Set<Film> getPopularFilms(Integer count) {
        return filmDbRepository.getPopularFilms(count);
    }
}
