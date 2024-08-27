package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaRepository {
    List<Mpa> getAll();

    Optional<Mpa> getById(Integer id);

    Mpa addRating(Mpa rating);
}
