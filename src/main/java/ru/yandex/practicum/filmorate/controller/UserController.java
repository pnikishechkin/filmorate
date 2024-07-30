package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserStorage userStorage;

    public UserController(UserService userService) {
        this.userService = userService;
        this.userStorage = userService.getUserStorage();
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userStorage.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<Integer> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Integer> getCommonFriends(@PathVariable Integer id,
                                 @PathVariable Integer otherId) {
        Set<Integer> res = userService.getFriends(id);
        res.retainAll(userService.getFriends(otherId));
        return res;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        userService.checkUser(user);

        if (user.getName() == null || user.getName().isEmpty()) {
            log.debug("Имя пользователя не задано, задание логина в качестве имени");
            user.setName(user.getLogin());
        }

        log.debug("Добавление нового пользователя с идентификатором {}", user.getId());
        return userStorage.addUser(user);
    }

    @PutMapping
    public User editUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.error("Ошибка! Идентификатор пользователя не задан");
            throw new ValidationException("Ошибка! Идентификатор пользователя не задан");
        }
        if (userStorage.containsUserById(user.getId())) {
            userService.checkUser(user);

            if (user.getName() == null || user.getName().isEmpty()) {
                log.debug("Имя пользователя не задано, задание логина в качестве имени");
                user.setName(user.getLogin());
            }

            return userStorage.editUser(user);
        } else {
            log.error("Ошибка! Пользователя с заданным идентификатором не существует");
             throw new ValidationException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

}
