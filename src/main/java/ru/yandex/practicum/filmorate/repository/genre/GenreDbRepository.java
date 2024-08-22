package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.*;

@Repository
public class GenreDbRepository extends BaseDbRepository<Genre> {
    public GenreDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    private static final String SQL_GET_GENRES_BY_FILM_ID =
            "SELECT * FROM genres as g LEFT JOIN films_genres as fg ON g.genre_id = fg.genre_id WHERE film_id = " +
                    ":film_id ORDER BY g.genre_id;";

    private static final String SQL_GET_GENRES_ID_BY_FILM_ID =
            "SELECT genre_id FROM films_genres WHERE film_id = :film_id;";

    private static final String SQL_GET_ALL_GENRES =
            "select * from GENRES;";

    private static final String SQL_GET_GENRES_BY_ID =
            "select * from GENRES WHERE genre_id=:id;";

    public List<Genre> getAll() {
        return getMany(SQL_GET_ALL_GENRES);
    }

    public Optional<Genre> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_GENRES_BY_ID, params);
    }

    public Set<Genre> getGenresByFilmId(Integer filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        return new LinkedHashSet<>(jdbc.query(SQL_GET_GENRES_BY_FILM_ID, params, mapper));
    }

    public Set<Integer> getGenresIdByFilmId(Integer filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        return new LinkedHashSet<>(jdbc.query(SQL_GET_GENRES_ID_BY_FILM_ID, params,
                new SingleColumnRowMapper<>(Integer.class)));
    }

}
