package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    List<Genre> getGenres();

    Genre addGenre(Genre genre);

    Optional<Genre> editGenre(Genre genre);

    void deleteGenre(Genre genre);

    boolean containsGenreById(Integer id);

    Optional<Genre> getGenreById(Integer id);
}
