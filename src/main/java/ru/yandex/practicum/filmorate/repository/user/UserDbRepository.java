package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;

import java.util.*;

@Repository
public class UserDbRepository extends BaseDbRepository<User> implements UserRepository {

    public UserDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper, FilmDbRepository filmDbRepository) {
        super(jdbc, mapper);
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

    private static final String SQL_GET_USER_FRIENDS =
            "SELECT * FROM users WHERE user_id IN " +
                    "(SELECT friend_id FROM users_friends WHERE user_id=:user_id);";

    private static final String SQL_INSERT_USER_FRIEND =
            "INSERT INTO users_friends (user_id, friend_id) " +
                    "VALUES (:user_id, :friend_id);";

    private static final String SQL_DELETE_USER_FRIEND =
            "DELETE FROM users_friends WHERE user_id=:user_id AND friend_id=:friend_id;";

    private static final String SQL_UPDATE_USER =
            "UPDATE users SET email=:email, login=:login, user_name=:user_name, " +
                    "birthday=:birthday WHERE user_id=:user_id;";

    private static final String SQL_GET_COMMON_USER =
            "SELECT * FROM users WHERE user_id IN (SELECT uf1.friend_id FROM users_friends AS uf1 " +
                    "INNER JOIN users_friends AS uf2 ON uf1.FRIEND_ID = uf2.FRIEND_ID " +
                    "WHERE uf1.user_id=:id AND uf2.user_id=:other_id);";

    @Override
    public List<User> getAll() {
        return getMany(SQL_GET_ALL_USERS);
    }

    @Override
    public Optional<User> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<User> user = getOne(SQL_GET_USER_BY_ID, params);
        return user;
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return getMany(SQL_GET_USER_FRIENDS, Map.of("user_id", id));
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
        params.addValue("user_id", user.getId());

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
