package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;
import ru.yandex.practicum.filmorate.repository.rating.RatingDbRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@Qualifier("filmDbRepository")
public class FilmDbRepository extends BaseDbRepository<Film> {

    private final GenreDbRepository genreDbRepository;
    private final RatingDbRepository ratingDbRepository;

    public FilmDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbRepository genreDbRepository, RatingDbRepository ratingDbRepository) {
        super(jdbc, mapper, "films", "film");
        this.genreDbRepository = genreDbRepository;
        this.ratingDbRepository = ratingDbRepository;
    }

    private static final String SQL_GET_ALL_FILMS =
            "SELECT * FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id;";

    private static final String SQL_GET_FILM_BY_ID =
            "SELECT * FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id WHERE f.film_id = :id;";

    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (rating_id, film_name, description, release_date, duration) " +
                    "VALUES (:rating_id, :film_name, :description, :release_date, :duration) returing film_id";

    @Override
    public List<Film> getAll() {
        // Получаем все фильмы с включенными данными рейтинга
        List<Film> films = jdbc.query(SQL_GET_ALL_FILMS, mapper);
        // Получаем список всех жанров
        List<Genre> genres = genreDbRepository.getAll();
        // Добавляем объекты жанров для каждого фильма
        films.stream().forEach(film -> film
                .setGenres(
                        genres
                                .stream()
                                .filter(genre -> genreDbRepository.getGenresIdByFilmId(film.getId()).contains(genre.getId()))
                                .collect(Collectors.toSet())
                )
        );

        // Альтернативное решение: записываем список объектов жанров для каждого фильма из базы
        // films.stream().forEach(film -> film.setGenres(genreDbRepository.getGenresByFilmId(film.getId())));

        return films;
    }

    @Override
    public Optional<Film> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<Film> film = getOne(SQL_GET_FILM_BY_ID, params);
        // Записать объекты жанров
        film.ifPresent(f -> f.setGenres(genreDbRepository.getGenresByFilmId(f.getId())));
        return film;
    }

    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();



        params.addValue("rating_id", film.getRating().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        jdbc.update(SQL_INSERT_FILM, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));
        return film;
    }

/*
    @Override
    public Optional<Film> editFilm(Film film) {
        return Optional.empty();
    }

    @Override
    public void deleteFilm(Film film) {

    }

    @Override
    public boolean containsFilmById(Integer id) {
        return false;
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {

//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("id", id);

        return Optional.empty();
    }
 */
}
