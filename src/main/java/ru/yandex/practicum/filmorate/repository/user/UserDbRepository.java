package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления пользователями
 */
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

    /**
     * Получить список пользователей
     *
     * @return список пользователей
     */
    @Override
    public List<User> getAll() {
        return getMany(SQL_GET_ALL_USERS);
    }

    /**
     * Получить пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return объект пользователя (опционально)
     */
    @Override
    public Optional<User> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<User> user = getOne(SQL_GET_USER_BY_ID, params);
        return user;
    }

    /**
     * Получить список друзей пользователя
     *
     * @param id идентификатор пользователя
     * @return спсиок объектов друзей пользователя
     */
    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return getMany(SQL_GET_USER_FRIENDS, Map.of("user_id", id));
    }

    /**
     * Добавить нового пользователя
     *
     * @param user объект добавляемого пользователя
     * @return объект добавленного пользователя
     */
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

    /**
     * Добавить в друзья
     *
     * @param userId   пользователь, кто хочет добавить в друзья
     * @param friendId пользователь, кого надо добавить в друзья
     */
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        // Добавление записи в таблицу users
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update(SQL_INSERT_USER_FRIEND, params);
    }

    /**
     * Обновить данные пользователя
     *
     * @param user объект изменяемого пользователя
     * @return измененный пользователь
     */
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

    /**
     * Удалить из друзей
     *
     * @param userId   идентификтор пользователя, кто удаляет из друзей
     * @param friendId идентификатор пользователя, кого удаляют из друзей
     */
    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbc.update(SQL_DELETE_USER_FRIEND,
                Map.of("user_id", userId, "friend_id", friendId));
    }

    /**
     * Получить список общих друзей между двумя пользователями
     *
     * @param id      первый пользователь
     * @param otherId второй пользователь
     * @return множество с объектами пользователей, являющихся общими друзьями для заданных
     */
    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        return new LinkedHashSet<>(getMany(SQL_GET_COMMON_USER,
                Map.of("id", id, "other_id", otherId)));
    }

    public List<Integer> getIdFilmsLikesByUser(Integer userId) {
        String sql = "SELECT film_id FROM users_films_likes WHERE user_id = :user_id;";
        return jdbc.queryForList(sql, Map.of("user_id", userId), Integer.class);
    }

    public Set<Film> getRecommendations(Integer userId) {

        Map<Integer, List<Integer>> userIdFilmsLikes = new HashMap<>();
        List<User> users = this.getAll();

        System.out.println("Исходный: " + userId);

        // Наполняем мапу "идентификатор пользователя -> список идентификаторов пролайканных им фильмов"
        for (User user : users) {
            userIdFilmsLikes.put(user.getId(), this.getIdFilmsLikesByUser(user.getId()));
            System.out.println(user.getId() + " | " + this.getIdFilmsLikesByUser(user.getId()));
        }

        long maxCount = 0;
        Set<Integer> overlap = new HashSet<>();

        // Определяем список фильмов, рекомендованных пользователю на основе предпочтений других пользователей
        for (Integer id : userIdFilmsLikes.keySet()) {

            // Пропускаем поиск для нашего пользователя
            if (id.equals(userId)) continue;

            // Определяем количество пересечений по лайканным фильмам с другими
            Integer numberOfOverlap = (int) userIdFilmsLikes.get(id).stream()
                    .filter(filmId -> userIdFilmsLikes.get(userId).contains(filmId)).count();

            System.out.println(id + " - " + numberOfOverlap);

            // В случае равенства с максимальным, записываем
            if (numberOfOverlap == maxCount && numberOfOverlap != 0) {
                overlap.add(id);
            }

            // Определили новое максимальное значение пересечений
            if (numberOfOverlap > maxCount) {
                maxCount = numberOfOverlap;
                overlap = new HashSet<>();
                overlap.add(id);
            }
        }

        System.out.println(overlap);
        if (maxCount == 0)
            return new HashSet<>();
        else
            return overlap.stream().flatMap(idUser -> this.getIdFilmsLikesByUser(idUser).stream())
                .filter(filmId -> !userIdFilmsLikes.get(userId).contains(filmId))
                .map(filmId -> filmDbRepository.getById(filmId))
                .collect(Collectors.toSet());
    }
}
