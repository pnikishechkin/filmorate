package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film addFilm(Film film);

    Optional<Film> editFilm(Film film);

    void deleteFilm(Film film);

    boolean containsFilmById(Integer id);

    Optional<Film> getFilmById(Integer id);
}
