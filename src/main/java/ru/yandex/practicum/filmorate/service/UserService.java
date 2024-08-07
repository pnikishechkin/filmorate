package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
    }

    public User addUser(User user) {
        this.checkUser(user);
        return userStorage.addUser(user);
    }

    public User editUser(User user) {
        User res = userStorage.editUser(user).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
        this.checkUser(res);
        return res;
    }

    public Collection<User> addFriend(Integer firstUserId, Integer secondUserId) {
        User user1 = userStorage.getUserById(firstUserId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
        User user2 = userStorage.getUserById(secondUserId).orElseThrow(() -> new NotFoundException("Ошибка! " +
                "Пользователя с заданным идентификатором не существует"));
        user1.getFriendsId().add(secondUserId);
        user2.getFriendsId().add(firstUserId);
        return List.of(user1, user2);
    }

    public void deleteFriend(Integer firstUserId, Integer secondUserId) {

        User user1 = userStorage.getUserById(firstUserId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
        User user2 = userStorage.getUserById(secondUserId).orElseThrow(() -> new NotFoundException("Ошибка! " +
                "Пользователя с заданным идентификатором не существует"));
        user1.getFriendsId().remove(secondUserId);
        user2.getFriendsId().remove(firstUserId);

    }

    public Set<User> getFriends(Integer userId) {
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
        return userStorage.getFriends(userId);
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        Set<User> res = this.getFriends(id);
        res.retainAll(this.getFriends(otherId));
        return res;
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка! Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Ошибка! Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка! Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Ошибка! Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка! Дата рождения не может быть в будущем");
            throw new ValidationException("Ошибка! Дата рождения не может быть в будущем");
        }
    }

}
