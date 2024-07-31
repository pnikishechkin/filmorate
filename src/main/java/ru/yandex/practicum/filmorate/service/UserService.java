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
        if (userStorage.containsUserById(id)) {
            return userStorage.getUserById(id);
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

    public User addUser(User user) {
        this.checkUser(user);

        if (user.getName() == null || user.getName().isEmpty()) {
            log.debug("Имя пользователя не задано, задание логина в качестве имени");
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    public User editUser(User user) {
        if (userStorage.containsUserById(user.getId())) {
            this.checkUser(user);
            if (user.getName() == null || user.getName().isEmpty()) {
                log.debug("Имя пользователя не задано, задание логина в качестве имени");
                user.setName(user.getLogin());
            }
            return userStorage.editUser(user);
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

    public Collection<User> addFriend(Integer firstUserId, Integer secondUserId) {
        if (userStorage.containsUserById(firstUserId) && userStorage.containsUserById(secondUserId)) {
            userStorage.getUserById(firstUserId).getFriendsId().add(secondUserId);
            userStorage.getUserById(secondUserId).getFriendsId().add(firstUserId);
            return List.of(userStorage.getUserById(firstUserId), userStorage.getUserById(secondUserId));
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

    public void deleteFriend(Integer firstUserId, Integer secondUserId) {
        if (userStorage.containsUserById(firstUserId) && userStorage.containsUserById(secondUserId)) {
            userStorage.getUserById(firstUserId).getFriendsId().remove(secondUserId);
            userStorage.getUserById(secondUserId).getFriendsId().remove(firstUserId);
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

    public Set<User> getFriends(Integer userId) {
        if (userStorage.containsUserById(userId)) {
            return userStorage.getFriends(userId);
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
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
