package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        this.checkUser(user);
        user.setId(getNewId());

        if (user.getName() == null || user.getName().isEmpty()) {
            log.debug("Имя пользователя не задано, задание логина в качестве имени");
            user.setName(user.getLogin());
        }

        log.debug("Добавление нового пользователя с идентификатором {}", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User editUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.error("Ошибка! Идентификатор пользователя не задан");
            throw new ValidationException("Ошибка! Идентификатор пользователя не задан");
        }
        if (users.containsKey(user.getId())) {
            checkUser(user);

            if (user.getName() == null || user.getName().isEmpty()) {
                log.debug("Имя пользователя не задано, задание логина в качестве имени");
                user.setName(user.getLogin());
            }

            log.debug("Изменение параметров пользователя с идентификатором {}", user.getId());
            User oldUser = users.get(user.getId());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());
            oldUser.setLogin(user.getLogin());
            oldUser.setEmail(user.getEmail());
            return oldUser;
        }
        log.error("Ошибка! Пользователя с заданным идентификатором не существует");
        throw new ValidationException("Ошибка! Пользователя с заданным идентификатором не существует");
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

    private int getNewId() {
        int maxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }
}
