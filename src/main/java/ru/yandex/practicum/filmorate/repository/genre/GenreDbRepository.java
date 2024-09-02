package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.*;

/**
 * Репозиторий для управления жанрами фильмов
 */
@Repository
public class GenreDbRepository extends BaseDbRepository<Genre> implements GenreRepository {
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

    private static final String SQL_GET_GENRES_BY_IDs =
            "select * from GENRES WHERE genre_id IN (:ids);";

    /**
     * Получить список всех жанров фильмов
     *
     * @return список жанров
     */
    @Override
    public List<Genre> getAll() {
        return getMany(SQL_GET_ALL_GENRES);
    }

    /**
     * Получить жанр по идентификатору
     *
     * @param id идентификатор жанра
     * @return объект жанра (опционально)
     */
    @Override
    public Optional<Genre> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_GENRES_BY_ID, params);
    }

    /**
     * Получить множество жанров по идентификатору фильма
     *
     * @param filmId идентификатор фильма
     * @return множество жанров
     */
    @Override
    public Set<Genre> getGenresByFilmId(Integer filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        return new LinkedHashSet<>(jdbc.query(SQL_GET_GENRES_BY_FILM_ID, params, mapper));
    }

    /**
     * Получить список идентификаторов жанров по идентификатору фильма
     *
     * @param filmId идентификатор фильма
     * @return множество идентификаторов жанров
     */
    @Override
    public Set<Integer> getGenresIdByFilmId(Integer filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        return new LinkedHashSet<>(jdbc.query(SQL_GET_GENRES_ID_BY_FILM_ID, params,
                new SingleColumnRowMapper<>(Integer.class)));
    }

    /**
     * Получить жанры по идентификаторам
     *
     * @param genreIds множество идентификаторов
     * @return множество объектов жанров
     */
    public Set<Genre> getByIds(Set<Integer> genreIds) {
        return new LinkedHashSet<>(jdbc.query(SQL_GET_GENRES_BY_IDs,
                Map.of("ids", genreIds),
                mapper));
    }
}
