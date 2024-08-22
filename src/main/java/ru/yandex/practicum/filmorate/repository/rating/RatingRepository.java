package ru.yandex.practicum.filmorate.repository.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    List<Mpa> getAll();

    Optional<Mpa> getById(Integer id);

    Mpa addRating(Mpa rating);
}
