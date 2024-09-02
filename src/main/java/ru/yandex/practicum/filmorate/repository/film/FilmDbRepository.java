package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления фильмами
 */
@Repository
public class FilmDbRepository extends BaseDbRepository<Film> implements FilmRepository {

    private final GenreDbRepository genreDbRepository;
    private final FilmExtractor filmExtractor;
    private final FilmGenreRowMapper filmGenreRowMapper;

    public FilmDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbRepository genreDbRepository, FilmExtractor filmExtractor, FilmGenreRowMapper filmGenreRowMapper) {
        super(jdbc, mapper);
        this.genreDbRepository = genreDbRepository;
        this.filmExtractor = filmExtractor;
        this.filmGenreRowMapper = filmGenreRowMapper;
    }

    private static final String SQL_GET_ALL_FILMS =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id;";

    private static final String SQL_GET_FILM_BY_ID_JOIN_GENRES =
            "SELECT * FROM films AS f " +
                    "LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                    "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres as g ON g.genre_id = fg.genre_id " +
                    "WHERE f.film_id = :id " +
                    "ORDER BY genre_id;";

    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (mpa_id, film_name, description, release_date, duration) " +
                    "VALUES (:mpa_id, :film_name, :description, :release_date, :duration);";

    private static final String SQL_INSERT_FILMS_GENRES =
            "INSERT INTO films_genres (film_id, genre_id) " +
                    "VALUES (:film_id, :genre_id);";

    private static final String SQL_INSERT_USER_FILMS_LIKES =
            "INSERT INTO users_films_likes (user_id, film_id) " +
                    "VALUES (:user_id, :film_id);";

    private static final String SQL_DELETE_FILMS_GENRES =
            "DELETE FROM films_genres WHERE film_id=:film_id";

    private static final String SQL_DELETE_FILM =
            "DELETE FROM films WHERE film_id=:film_id";

    private static final String SQL_UPDATE_FILM =
            "UPDATE films SET mpa_id=:mpa_id, film_name=:film_name, description=:description, " +
                    "release_date=:release_date, duration=:duration WHERE film_id=:film_id;";

    private static final String SQL_GET_FILM_IDs_LIKE_USER =
            "SELECT film_id FROM users_films_likes WHERE user_id=:user_id;";

    private static final String SQL_GET_FILMS_BY_IDs =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id WHERE f.film_id IN (:ids);";

    private static final String SQL_DELETE_USER_FILMS_LIKES =
            "DELETE FROM users_films_likes WHERE user_id=:user_id AND film_id=:film_id;";

    private static final String SQL_GET_POPULAR_FILMS =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                    "WHERE film_id IN " +
                    "(SELECT film_id FROM USERS_FILMS_LIKES GROUP BY film_id ORDER BY COUNT(film_id) DESC) " +
                    "LIMIT :count;";

    private static final String SQL_GET_FILMS_GENRES =
            "SELECT * FROM films_genres";

    /**
     * Получить все фильмы
     * @return список всех фильмов
     */
    @Override
    public List<Film> getAll() {
        return this.getFilms(SQL_GET_ALL_FILMS, Map.of());
    }

    /**
     * Получить фильм по идентификатору
     * @param id идентификатор фильма
     * @return опционально - объект фильма
     */
    @Override
    public Optional<Film> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<Film> film;
        try {
            // Получаем фильм с всеми жанрами, используяю 1 запрос с JOIN
            Film res = jdbc.query(SQL_GET_FILM_BY_ID_JOIN_GENRES, params, filmExtractor);
            film = Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException ignored) {
            film = Optional.empty();
        }
        return film;
    }

    /**
     * Добавить новый фильм
     * @param film объект добавляемого фильма
     * @return объект добавленного фильма
     */
    @Override
    public Film addFilm(Film film) {
        // Добавление записи в таблицу films
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        jdbc.update(SQL_INSERT_FILM, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));

        // Batch добавление связей фильма с жанрами
        addGenresToDb(film);

        // Получение полного объекта фильма из базы (необходимо для полного наполнения объектов Mpa и Genres)
        film = getById(film.getId()).orElseThrow(() -> new NotFoundException("Ошибка при добавлении фильма"));
        return film;
    }

    /**
     * Удалить фильм
     * @param film удаляемый фильм
     * @return флаг, был ли удален фильм
     */
    @Override
    public Boolean deleteFilm(Film film) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        // Удаление связей фильма с жанрами
        jdbc.update(SQL_DELETE_FILMS_GENRES, params);
        // Удаление фильма
        int res = jdbc.update(SQL_DELETE_FILM, params);

        return (res == 1);
    }

    /**
     * Редактирование фильма
     * @param film объект изменяемого фильма
     * @return объект измененного фильма
     */
    @Override
    public Film updateFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        // Обновление записи описания фильма
        jdbc.update(SQL_UPDATE_FILM, params);

        // Удаление связей фильма с жанрами
        jdbc.update(SQL_DELETE_FILMS_GENRES, params);

        // Batch добавление связей фильма с жанрами
        addGenresToDb(film);

        // Получение обновленного полного объекта фильма из базы (необходимо для наполнения объектов Mpa и Genres)
        return getById(film.getId()).get();
    }

    /**
     * Добавить лайк от выбранного пользователя указанному фильму
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @Override
    public void adduserLike(Integer filmId, Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);
        jdbc.update(SQL_INSERT_USER_FILMS_LIKES, params);
    }

    /**
     * Удалить лайк выбранного пользователя указанному фильму
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @Override
    public void deleteUserLike(Integer filmId, Integer userId) {
        jdbc.update(SQL_DELETE_USER_FILMS_LIKES,
                Map.of("film_id", filmId, "user_id", userId));
    }

    /**
     * Получить множество фильмов, которые лайкнул указанный пользователь
     * @param userId идентификатор пользователя
     * @return множество фильмов
     */
    @Override
    public Set<Film> getLikeFilmsByUserId(Integer userId) {
        // Находим список идентификаторов фильмов, на которых есть лайк указанного пользователя
        Set<Integer> filmIds = new HashSet<>(jdbc.query(SQL_GET_FILM_IDs_LIKE_USER, Map.of("user_id", userId),
                new SingleColumnRowMapper<>(Integer.class)));

        List<Film> films = getMany(SQL_GET_FILMS_BY_IDs, Map.of("ids", filmIds));

        // Возвращаем множество объектов фильмов, на которых есть лайк пользователя
        return new LinkedHashSet<>(films);
    }

    /**
     * Получить список популярных фильмов
     * @param count количество выводимых фильмов
     * @return список фильмов
     */
    @Override
    public List<Film> getPopularFilms(Integer count) {
        return this.getFilms(SQL_GET_POPULAR_FILMS, Map.of("count", count));
    }

    /**
     * Получить список фильмов по заданному запросу, в связке с жанрами
     * @param query SQL запрос
     * @param map параметры запроса
     * @return список фильмов
     */
    private List<Film> getFilms(String query, Map<String, Object> map) {
        // Получаем все фильмы с включенными данными рейтинга
        List<Film> films = jdbc.query(query, map, mapper);
        films.forEach(f -> f.setGenres(new LinkedHashSet<>()));
        LinkedHashMap<Integer, Film> filmsMap = new LinkedHashMap<>(films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity())));

        // Получаем список всех жанров
        List<Genre> genres = genreDbRepository.getAll();
        LinkedHashMap<Integer, Genre> genresMap = new LinkedHashMap<>(genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity())));

        // Получаем все связи фильмы-жанры
        List<FilmGenre> relation = jdbc.query(SQL_GET_FILMS_GENRES, filmGenreRowMapper);

        // Заполняем жанры для всех фильмов
        relation.forEach(fg -> {
                    if (filmsMap.containsKey(fg.getFilmId())) {
                        filmsMap.get(fg.getFilmId()).getGenres().add(
                                genresMap.get(fg.getGenreId()));
                    }
                }
        );

        return films;
    }

    /**
     * Добавление связей фильма с жанрами в БД (Batch)
     *
     * @param film объект фильма
     */
    private void addGenresToDb(Film film) {

        MapSqlParameterSource params;

        // Batch обновление связей фильма с жанрами
        if (film.getGenres() != null) {
            List<SqlParameterSource> listParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                params = new MapSqlParameterSource();
                params.addValue("film_id", film.getId());
                params.addValue("genre_id", genre.getId());
                listParams.add(params);
            }
            jdbc.batchUpdate(SQL_INSERT_FILMS_GENRES, listParams
                    .toArray(new SqlParameterSource[listParams.size()]));
        }
    }
}
