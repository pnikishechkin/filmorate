package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    List<Film> getAll();

    Optional<Film> getById(Integer id);

    Film addFilm(Film film);

    Boolean deleteFilm(Integer id);

    Film updateFilm(Film film);

    void adduserLike(Integer filmId, Integer userId);

    void deleteUserLike(Integer filmId, Integer userId);

    Set<Film> getLikeFilmsByUserId(Integer userId);

    List<Film> getPopularFilms(Integer count);
}
