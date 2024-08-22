package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;

import java.util.*;

@Repository
public class UserDbRepository extends BaseDbRepository<User> implements UserRepository {

    private final FilmDbRepository filmDbRepository;

    public UserDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper, FilmDbRepository filmDbRepository) {
        super(jdbc, mapper);
        this.filmDbRepository = filmDbRepository;
    }

    private static final String SQL_GET_ALL_USERS =
            "SELECT * FROM users;";

    private static final String SQL_GET_USER_BY_ID =
            "SELECT * FROM users WHERE user_id=:id;";

    private static final String SQL_GET_USERS_BY_IDs =
            "SELECT * FROM users WHERE user_id IN (:ids);";

    private static final String SQL_INSERT_USER =
            "INSERT INTO users (email, login, user_name, birthday) " +
                    "VALUES (:email, :login, :user_name, :birthday);";

    private static final String SQL_GET_USER_FRIENDS_IDs =
            "SELECT friend_id FROM users_friends WHERE user_id=:user_id;";

    private static final String SQL_INSERT_USER_FRIEND =
            "INSERT INTO users_friends (user_id, friend_id) " +
                    "VALUES (:user_id, :friend_id);";

    private static final String SQL_DELETE_USERS_FILMS_LIKES =
            "DELETE FROM users_films_likes WHERE user_id=:user_id";

    private static final String SQL_DELETE_USER_FRIEND =
            "DELETE FROM users_friends WHERE user_id=:user_id AND friend_id=:friend_id;";

    private static final String SQL_UPDATE_USER =
            "UPDATE users SET email=:email, login=:login, user_name=:user_name, " +
                    "birthday=:birthday WHERE user_id=:user_id;";
/*
* SELECT * FROM users WHERE user_id IN
(SELECT uf1.friend_id FROM users_friends AS uf1
                 INNER JOIN users_friends AS uf2
                 ON uf1.FRIEND_ID = uf2.FRIEND_ID
                 WHERE uf1.user_id=2 AND uf2.user_id=1);*/

    private static final String SQL_GET_COMMON_USER =
            "SELECT * FROM users WHERE user_id IN (SELECT uf1.friend_id FROM users_friends AS uf1 " +
                    "INNER JOIN users_friends AS uf2 ON uf1.FRIEND_ID = uf2.FRIEND_ID " +
                    "WHERE uf1.user_id=:id AND uf2.user_id=:other_id);";

    /*
        private static final String SQL_INSERT_FILMS_GENRES =
                "INSERT INTO films_genres (film_id, genre_id) " +
                        "VALUES (:film_id, :genre_id);";

        private static final String SQL_DELETE_FILMS_GENRES =
                "DELETE FROM films_genres WHERE film_id=:film_id";

        private static final String SQL_DELETE_FILM =
                "DELETE FROM films WHERE film_id=:film_id";

        private static final String SQL_UPDATE_FILM =
                "UPDATE films SET rating_id=:rating_id, film_name=:film_name, description=:description, " +
                        "release_date=:release_date, duration=:duration WHERE film_id=:film_id;";
    */
    @Override
    public List<User> getAll() {
        return getMany(SQL_GET_ALL_USERS);
    }

    @Override
    public Optional<User> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<User> user = getOne(SQL_GET_USER_BY_ID, params);

        user.ifPresent(u -> {
            // Заполнение объекта с списком друзей
            u.setFriends(this.getFriendsByUserId(u.getId()));
            // Заполнение объекта с списком фильмов, которым пользователь поставил лайк
            Set<Film> likeFilms = filmDbRepository.getLikeFilmsByUserId(user.get().getId());
            u.setLikeFilms(likeFilms);
        });

        return user;
    }

    @Override
    public Set<User> getFriendsByUserId(Integer id) {
        // Находим список идентификаторов друзей
        Set<Integer> userIds = new HashSet<>(jdbc.query(SQL_GET_USER_FRIENDS_IDs, Map.of("user_id", id),
                new SingleColumnRowMapper<>(Integer.class)));

        // Возвращаем множество объектов пользователей, являющихся друзьями для запрашиваемого
        return new LinkedHashSet<>(getMany(SQL_GET_USERS_BY_IDs, Map.of("ids", userIds)));
    }

    @Override
    public User addUser(User user) {
        // Добавление записи в таблицу users
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("user_name", user.getName());
        params.addValue("birthday", user.getBirthday());

        jdbc.update(SQL_INSERT_USER, params, keyHolder);
        user.setId(keyHolder.getKeyAs(Integer.class));

        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        // Добавление записи в таблицу users
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update(SQL_INSERT_USER_FRIEND, params);
    }

    @Override
    public User updateUser(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());

        /*
        // Удаление связей пользователя с фильмами
        jdbc.update(SQL_DELETE_USERS_FILMS_LIKES, params);

        // Удаление записей пользователя с друзьями
        jdbc.update(SQL_DELETE_USERS_FRIENDS, params);

        // TODO batch обновление
        // Добавить лайки к фильмам
        for (Film film: user.getLikeFilms()) {
            filmDbRepository.adduserLike(film.getId(), user.getId());
        }

        // TODO batch обновление
        // Добавить записи друзей
        for (User u: user.getFriends()) {
            params = new MapSqlParameterSource();
            params.addValue("user_id", user.getId());
            params.addValue("friend_id", u.getId());
            jdbc.update(SQL_INSERT_USER_FRIEND, params);
        }
         */

        // Обновить данные пользователя
        params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("user_name", user.getName());
        params.addValue("birthday", user.getBirthday());

        // Обновление записи описания фильма
        jdbc.update(SQL_UPDATE_USER, params);

        // Получение обновленного полного объекта фильма из базы (необходимо для наполнения внутренних объектов)
        return getById(user.getId()).get();
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbc.update(SQL_DELETE_USER_FRIEND,
                Map.of("user_id", userId, "friend_id", friendId));
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        return new LinkedHashSet<>(getMany(SQL_GET_COMMON_USER,
                Map.of("id", id, "other_id", otherId)));
    }
}
