package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class FilmMemoryRepository implements FilmRepository {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return films.values().stream().toList();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNewId());
        //film.setUsersIdLike(new HashSet<>());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean containsFilmById(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Optional<Film> editFilm(Film film) {
        Film oldFilm = films.get(film.getId());
        if (oldFilm != null) {
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setDuration(film.getDuration());
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        return Optional.ofNullable(oldFilm);
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
