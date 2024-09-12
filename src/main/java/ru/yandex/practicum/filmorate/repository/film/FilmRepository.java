package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    List<Film> getAll();

    Optional<Film> getById(Integer id);

    Film addFilm(Film film);

    Boolean deleteFilm(Film film);

    Film updateFilm(Film film);

    void adduserLike(Integer filmId, Integer userId);

    void deleteUserLike(Integer filmId, Integer userId);

    Set<Film> getLikeFilmsByUserId(Integer userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getPopularFilmsWithGenreAndYear(Integer count, Integer year, Integer genreId);

    List<Film> getPopularFilmsWithGenre(Integer count, Integer genreId);

    List<Film> getPopularFilmsWithYear(Integer count, Integer year);

    List<Film> getPopularFilms(Integer count);
}
