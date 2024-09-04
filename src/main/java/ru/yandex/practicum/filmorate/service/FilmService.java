package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaDbRepository;
import ru.yandex.practicum.filmorate.repository.user.UserDbRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления фильмами
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class FilmService {

    private final FilmDbRepository filmDbRepository;
    private final MpaDbRepository mpaDbRepository;
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

        mpaDbRepository.getById(film.getMpa().getId()).orElseThrow(() ->
                new ValidationException("Ошибка! Рейтинга с заданным идентификатором не существует"));

        checkGenres(film);
        return filmDbRepository.addFilm(film);
    }

    public Boolean deleteFilm(Integer id) {

        filmDbRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));

        return filmDbRepository.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        log.debug("Изменение параметров фильма с идентификатором {}", film.getId());

        filmDbRepository.getById(film.getId()).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));

        mpaDbRepository.getById(film.getMpa().getId()).orElseThrow(() ->
                new NotFoundException("Ошибка! Рейтинга с заданным идентификатором не существует"));

        checkGenres(film);
        return filmDbRepository.updateFilm(film);
    }

    public void addUserLike(Integer filmId, Integer userId) {

        filmDbRepository.getById(filmId).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));

        userDbRepository.getById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));

        filmDbRepository.adduserLike(filmId, userId);
    }

    public void deleteUserLike(Integer filmId, Integer userId) {

        filmDbRepository.getById(filmId).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));

        userDbRepository.getById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));

        filmDbRepository.deleteUserLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDbRepository.getPopularFilms(count);
    }

    private void checkGenres(Film film) {
        if (film.getGenres() != null) {
            Set<Integer> genreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            Set<Genre> genres = genreDbRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new ValidationException("Ошибка! Жанра с заданным идентификатором не существует");
            }
        }
    }
}
