package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    List<Film> getFilms();

    Film addFilm(Film film);

    Optional<Film> editFilm(Film film);

    void deleteFilm(Film film);

    boolean containsFilmById(Integer id);

    Optional<Film> getFilmById(Integer id);
}
