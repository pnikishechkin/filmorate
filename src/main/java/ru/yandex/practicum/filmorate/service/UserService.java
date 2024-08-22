package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;
import ru.yandex.practicum.filmorate.repository.user.UserDbRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbRepository userDbRepository;
    private final FilmDbRepository filmDbRepository;

    public List<User> getUsers() {
        return userDbRepository.getAll();
    }

    public User getUserById(Integer id) {
        return userDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным " +
                "идентификатором не существует"));
    }

    public User addUser(User user) {
        this.checkUser(user);
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

    public Set<User> getFriends(Integer id) {
        User user = userDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным " +
                "идентификатором не существует"));
        return user.getFriends();
    }

//    public User editUser(User user) {
//        User res = userStorage.editUser(user).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
//                "заданным идентификатором не существует"));
//        this.checkUser(res);
//        return res;
//    }
//

//
//    public void deleteFriend(Integer firstUserId, Integer secondUserId) {
//
//        User user1 = userStorage.getUserById(firstUserId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
//        User user2 = userStorage.getUserById(secondUserId).orElseThrow(() -> new NotFoundException("Ошибка! " +
//                "Пользователя с заданным идентификатором не существует"));
//        user1.getFriendsId().remove(secondUserId);
//        user2.getFriendsId().remove(firstUserId);
//
//    }
//
//    public Set<User> getFriends(Integer userId) {
//        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
//        return userStorage.getFriends(userId);
//    }
//
//    public Set<User> getCommonFriends(Integer id, Integer otherId) {
//        Set<User> res = this.getFriends(id);
//        res.retainAll(this.getFriends(otherId));
//        return res;
//    }
//
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

    public User updateUser(User user) {
        log.debug("Изменение параметров пользователя с идентификатором {}", user.getId());

        if (userDbRepository.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
        /*
        user.getFriends().forEach(fr -> {
            if (userDbRepository.getById(fr.getId()).isEmpty()) {
                throw new NotFoundException("Ошибка! Друга пользователя с заданным идентификатором не существует");
            }
        });
        user.getLikeFilms().forEach(film -> {
            if (filmDbRepository.getById(film.getId()).isEmpty()) {
                throw new NotFoundException("Ошибка! Фильма (лайк пользователя) с заданным идентификатором не " +
                        "существует");
            }
        });
         */
        this.checkUser(user);
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
}
