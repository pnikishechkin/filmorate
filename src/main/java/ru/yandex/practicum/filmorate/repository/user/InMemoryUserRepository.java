package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.stereotype.Component;

@Component
public class InMemoryUserRepository {
/*
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getNewId());
        user.setFriendsId(new HashSet<>());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> editUser(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser != null) {
            if (user.getName() == null || user.getName().isEmpty()) {
                oldUser.setName(user.getLogin());
            } else {
                oldUser.setName(user.getName());
            }
            oldUser.setBirthday(user.getBirthday());
            oldUser.setLogin(user.getLogin());
            oldUser.setEmail(user.getEmail());
        }
        return Optional.ofNullable(oldUser);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
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

 */
}
