package ru.yandex.practicum.filmorate.repository.rating;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    List<Rating> getRatings();
    Optional<Rating> getRatingById(Integer id);
}
