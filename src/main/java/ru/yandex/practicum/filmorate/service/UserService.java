package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        checkExistUser(userId);
        checkExistUser(friendId);
        userDbRepository.addFriend(userId, friendId);
        return List.of(userDbRepository.getById(userId).get(), userDbRepository.getById(friendId).get());
    }

    public List<User> getFriends(Integer id) {
        checkExistUser(id);
        return userDbRepository.getFriendsByUserId(id);
    }

    public User updateUser(User user) {
        log.debug("Изменение параметров пользователя с идентификатором {}", user.getId());
        checkExistUser(user.getId());
        return userDbRepository.updateUser(user);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        userDbRepository.deleteFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        checkExistUser(id);
        checkExistUser(otherId);
        return userDbRepository.getCommonFriends(id, otherId);
    }

    public Boolean deleteUser(Integer id) {
        checkExistUser(id);
        return userDbRepository.deleteUser(id);
    }

    private void checkExistUser(Integer id) {
        userDbRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
    }
}
