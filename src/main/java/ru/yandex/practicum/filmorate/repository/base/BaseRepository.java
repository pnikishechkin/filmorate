package ru.yandex.practicum.filmorate.repository.base;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    List<T> getAll();
    Optional<T> getById(Integer id);
}
