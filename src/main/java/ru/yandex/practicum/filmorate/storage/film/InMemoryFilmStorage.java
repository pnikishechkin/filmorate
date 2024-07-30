package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean containsFilmById(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film editFilm(Film film) {
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setReleaseDate(film.getReleaseDate());
        return oldFilm;
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film);
    }

    private int getNewId() {
        int maxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }
}
