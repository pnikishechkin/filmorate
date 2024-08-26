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

}
