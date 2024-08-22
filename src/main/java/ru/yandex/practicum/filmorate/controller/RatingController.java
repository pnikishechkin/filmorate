package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<Mpa> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public Mpa getRating(@PathVariable Integer id) {
        return ratingService.getRatingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa addRating(@RequestBody Mpa rating) {
        return ratingService.addRating(rating);
    }

    /*
    При создании и получении фильмов достаточно передать список идентификаторов жанров и идентификатор рейтинга.
    Эти же данные должны передаваться при обновлении, создании и получении фильмов — если нужно,
    обновите эти эндпоинты.
    И последнее небольше изменение: дружба должна стать односторонней.
    Это значит, что если какой-то пользователь оставил вам заявку в друзья, то он будет в списке
    ваших друзей, а вы в его — нет.
     */
}
