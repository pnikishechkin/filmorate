package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbRepository genreDbRepository;

    public List<Genre> getGenres() {
        return genreDbRepository.getAll();
    }

    public Genre getGenreById(Integer id) {
        return genreDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Жанра с заданным " +
                "идентификатором не существует"));
    }
}
