package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public void addFriend(Integer firstUserId, Integer secondUserId) {
        if (userStorage.containsUserById(firstUserId) && userStorage.containsUserById(secondUserId)) {
            userStorage.getUserById(firstUserId).getFriends().add(secondUserId);
            userStorage.getUserById(secondUserId).getFriends().add(firstUserId);
        }
    }

    public void deleteFriend(Integer firstUserId, Integer secondUserId) {
        if (userStorage.containsUserById(firstUserId) && userStorage.containsUserById(secondUserId)) {
            userStorage.getUserById(firstUserId).getFriends().remove(secondUserId);
            userStorage.getUserById(secondUserId).getFriends().remove(firstUserId);
        }
    }

    public Set<Integer> getFriends(Integer userId) {
        if (userStorage.containsUserById(userId)) {
            return userStorage.getUserById(userId).getFriends();
        } else {
            return null;
        }
    }

    public void checkUser(User user) {
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
