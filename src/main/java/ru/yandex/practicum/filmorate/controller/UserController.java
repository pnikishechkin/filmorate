package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Контроллер для реализации API методов, связанных с пользователями
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    /**
     * Получить список пользователей
     *
     * @return список пользователей
     */
    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * Получить пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return объект пользователя (опционально)
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive Integer id) {
        return userService.getUserById(id);
    }

    /**
     * Добавить нового пользователя
     *
     * @param user объект добавляемого пользователя
     * @return объект добавленного пользователя
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Добавление нового пользователя с идентификатором {}", user.getId());
        return userService.addUser(user);
    }

    /**
     * Добавить в друзья
     *
     * @param id       пользователь, кто хочет добавить в друзья
     * @param friendId пользователь, кого надо добавить в друзья
     */
    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable Integer id,
                                      @PathVariable Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    /**
     * Получить список друзей пользователя
     *
     * @param id идентификатор пользователя
     * @return спсиок объектов друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Integer id) {
        return userService.getFriends(id);
    }

    /**
     * Обновить данные пользователя
     *
     * @param user объект изменяемого пользователя
     * @return измененный пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Удалить из друзей
     *
     * @param id       идентификтор пользователя, кто удаляет из друзей
     * @param friendId идентификатор пользователя, кого удаляют из друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriends(@PathVariable @Positive Integer id,
                              @PathVariable @Positive Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    /**
     * Получить список общих друзей между двумя пользователями
     *
     * @param id      первый пользователь
     * @param otherId второй пользователь
     * @return множество с объектами пользователей, являющихся общими друзьями для заданных
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable @Positive Integer id,
                                      @PathVariable @Positive Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    /**
     * Получить событие по идентификатору пользователя
     *
     * @param id идентификатор пользователя
     */
    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable @Positive Integer id) {
        return eventService.getByUserId(id);
    }

    /**
     * Получить рекомендации по идентификатору пользователя
     *
     * @param id идентификатор пользователя
     * @return сет фильмов
     */
    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendations(@PathVariable Integer id) {
        return userService.getRecommendations(id);
    }

    /**
     * Удалить пользователя
     *
     * @param id идентификатор удаляемого пользователя
     * @return флаг, был ли удален пользователь
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deleteUser(@PathVariable @Positive Integer id) {
        return userService.deleteUser(id);
    }
}