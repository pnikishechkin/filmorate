package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
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
    List<User> getAll();

    Optional<User> getById(Integer id);

    Set<User> getFriendsByUserId(Integer id);

    User addUser(User user);

    void addFriend(Integer userId, Integer friendId);

    User updateUser(User user);

    void deleteFriend(Integer userId, Integer friendId);

    Set<User> getCommonFriends(Integer id, Integer otherId);
}
