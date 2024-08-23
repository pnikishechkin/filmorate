package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
public class FilmDbRepository extends BaseDbRepository<Film> implements FilmRepository {

    private final GenreDbRepository genreDbRepository;

    public FilmDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbRepository genreDbRepository, RatingDbRepository ratingDbRepository) {
        super(jdbc, mapper);
        this.genreDbRepository = genreDbRepository;
    }

    private static final String SQL_GET_ALL_FILMS =
            "SELECT * FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id;";

    private static final String SQL_GET_FILM_BY_ID =
            "SELECT * FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id WHERE f.film_id = :id;";

    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (rating_id, film_name, description, release_date, duration) " +
                    "VALUES (:rating_id, :film_name, :description, :release_date, :duration);";

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
            "UPDATE films SET rating_id=:rating_id, film_name=:film_name, description=:description, " +
                    "release_date=:release_date, duration=:duration WHERE film_id=:film_id;";

    private static final String SQL_GET_FILM_IDs_LIKE_USER =
            "SELECT film_id FROM users_films_likes WHERE user_id=:user_id;";

    private static final String SQL_GET_FILMS_BY_IDs =
            "SELECT * FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id WHERE f.film_id IN (:ids);";

    private static final String SQL_DELETE_USER_FILMS_LIKES =
            "DELETE FROM users_films_likes WHERE user_id=:user_id AND film_id=:film_id;";

    private static final String SQL_GET_POPULAR_FILMS =
            "SELECT * FROM films AS f JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "WHERE film_id IN " +
                    "(SELECT film_id FROM USERS_FILMS_LIKES GROUP BY film_id ORDER BY COUNT(film_id) DESC) LIMIT " +
                    ":count;";

    @Override
    public List<Film> getAll() {
        // Получаем все фильмы с включенными данными рейтинга
        List<Film> films = jdbc.query(SQL_GET_ALL_FILMS, mapper);
        // Получаем список всех жанров
        List<Genre> genres = genreDbRepository.getAll();
        // Добавляем объекты жанров для каждого фильма
        films.forEach(film -> film
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
        if (film.isPresent()) {
            Set<Genre> genres = genreDbRepository.getGenresByFilmId(film.get().getId());
            if (genres != null) {
                film.get().setGenres(genres);
            }
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        // Добавление записи в таблицу films
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("rating_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        jdbc.update(SQL_INSERT_FILM, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));

        // TODO batch обновление
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                params = new MapSqlParameterSource();
                params.addValue("film_id", film.getId());
                params.addValue("genre_id", genre.getId());
                jdbc.update(SQL_INSERT_FILMS_GENRES, params);
            }
        }

        // Получение полного объекта фильма из базы (необходимо для полного наполнения объектов Rating и Genres)
        film = getById(film.getId()).orElseThrow(() -> new NotFoundException("Ошибка при добавлении фильма"));
        return film;
    }

    @Override
    public Boolean deleteFilm(Film film) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        // Удаление связей фильма с жанрами
        jdbc.update(SQL_DELETE_FILMS_GENRES, params);
        // Удаление фильма
        int res = jdbc.update(SQL_DELETE_FILM, params);

        // TODO удалить записи с лайками, связанные с этим фильмом

        return (res == 1);
    }

    @Override
    public Film updateFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());

        // Редактирование предполагает, что в запросе пришли данные с новыми жанрами фильма?

        // Удаление связей фильма с жанрами
//        jdbc.update(SQL_DELETE_FILMS_GENRES, params);

        // batch
        // Добавление связей фильма с жанрами
//        for (Genre genre : film.getGenres()) {
//            params = new MapSqlParameterSource();
//            params.addValue("film_id", film.getId());
//            params.addValue("genre_id", genre.getId());
//            jdbc.update(SQL_INSERT_FILMS_GENRES, params);
//        }

        params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("rating_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        // Обновление записи описания фильма
        jdbc.update(SQL_UPDATE_FILM, params);

        // Получение обновленного полного объекта фильма из базы (необходимо для наполнения объектов Rating и Genres)
        return getById(film.getId()).get();
    }

    @Override
    public void adduserLike(Integer filmId, Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);
        jdbc.update(SQL_INSERT_USER_FILMS_LIKES, params);
    }

    @Override
    public void deleteUserLike(Integer filmId, Integer userId) {
        jdbc.update(SQL_DELETE_USER_FILMS_LIKES,
                Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public Set<Film> getLikeFilmsByUserId(Integer userId) {
        // Находим список идентификаторов фильмов, на которых есть лайк указанного пользователя
        Set<Integer> filmIds = new HashSet<>(jdbc.query(SQL_GET_FILM_IDs_LIKE_USER, Map.of("user_id", userId),
                new SingleColumnRowMapper<>(Integer.class)));

        System.out.println("start");
        List<Film> films = getMany(SQL_GET_FILMS_BY_IDs, Map.of("ids", filmIds));
        System.out.println(films);

        // Возвращаем множество объектов фильмов, на которых есть лайк пользователя
        return new LinkedHashSet<>(films);
    }

    @Override
    public Set<Film> getPopularFilms(Integer count) {
        Set<Film> films = new LinkedHashSet<>(getMany(SQL_GET_POPULAR_FILMS, Map.of("count", count)));
        // Получаем список всех жанров
        List<Genre> genres = genreDbRepository.getAll();
        // Добавляем объекты жанров для каждого фильма
        films.forEach(film -> film
                .setGenres(
                        genres
                                .stream()
                                .filter(genre -> genreDbRepository.getGenresIdByFilmId(film.getId()).contains(genre.getId()))
                                .collect(Collectors.toSet())
                )
        );
        return films;
    }
}
