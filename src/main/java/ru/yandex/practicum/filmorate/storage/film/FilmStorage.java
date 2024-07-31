package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film addFilm(Film film);

    Film editFilm(Film film);

    void deleteFilm(Film film);

    boolean containsFilmById(Integer id);

    Film getFilmById(Integer id);
}
