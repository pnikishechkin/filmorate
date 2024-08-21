package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;
import ru.yandex.practicum.filmorate.repository.base.BaseRepository;

import java.util.List;

@RequiredArgsConstructor
public class BaseService<T> {
    protected final BaseRepository<T> repository;

    public List<T> getAll() {
        return repository.getAll();
    }

    public T getById(Integer id) {
        return repository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Жанра с заданным " +
                "идентификатором не существует"));
    }
}
