package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserDbRepository;

import java.util.List;
import java.util.Set;

/**
 * Сервисный класс для управления пользователями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbRepository userDbRepository;

    public List<User> getUsers() {
        return userDbRepository.getAll();
    }

    public User getUserById(Integer id) {
        return userDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным " +
                "идентификатором не существует"));
    }

    public User addUser(User user) {
        return userDbRepository.addUser(user);
    }

    public List<User> addFriend(Integer userId, Integer friendId) {

        userDbRepository.getById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным " +
                "идентификатором не существует"));

        userDbRepository.getById(friendId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным " +
                "идентификатором не существует"));

        userDbRepository.addFriend(userId, friendId);

        return List.of(userDbRepository.getById(userId).get(), userDbRepository.getById(friendId).get());
    }

    public List<User> getFriends(Integer id) {
        User user = userDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным " +
                "идентификатором не существует"));

        return userDbRepository.getFriendsByUserId(id);
    }

    public User updateUser(User user) {
        log.debug("Изменение параметров пользователя с идентификатором {}", user.getId());

        if (userDbRepository.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }

        return userDbRepository.updateUser(user);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userDbRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        if (userDbRepository.getById(friendId).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        userDbRepository.deleteFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        if (userDbRepository.getById(id).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        if (userDbRepository.getById(otherId).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        return userDbRepository.getCommonFriends(id, otherId);
    }

//    public List<Long> getUserFilm(Integer userId) {
//        return userDbRepository.getUserFilm(userId);
//    }

    public Set<Film> getRecommendations(Integer id) {
        if (userDbRepository.getById(id).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        return userDbRepository.getRecommendations(id);
    }
}
