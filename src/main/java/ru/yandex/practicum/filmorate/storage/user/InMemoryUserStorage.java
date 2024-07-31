package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getNewId());
        user.setFriendsId(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User editUser(User user) {
        User oldUser = users.get(user.getId());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        return oldUser;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user);
    }

    @Override
    public boolean containsUserById(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Set<User> getFriends(Integer userId) {
        User user = users.get(userId);
        return user.getFriendsId().stream().map(users::get).collect(Collectors.toSet());
    }

    private int getNewId() {
        int maxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }
}
