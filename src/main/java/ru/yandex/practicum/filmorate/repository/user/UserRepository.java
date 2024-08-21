package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Collection<User> getUsers();

    User addUser(User user);

    Optional<User> editUser(User user);

    Optional<User> getUserById(Integer id);

    void deleteUser(User user);

    Set<User> getFriends(Integer userId);

    boolean containsUserById(Integer id);
}
