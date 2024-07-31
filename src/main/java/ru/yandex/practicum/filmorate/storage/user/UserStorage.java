package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> getUsers();

    User addUser(User user);

    User editUser(User user);

    User getUserById(Integer id);

    void deleteUser(User user);

    Set<User> getFriends(Integer userId);

    boolean containsUserById(Integer id);
}
