package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorDbRepository;


import java.util.List;

/**
 * Сервисный класс для управления режиссерами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbRepository directorDbRepository;

    public List<Director> getAll() {
        log.info("Получение списка всех директоров.");
        return directorDbRepository.getAll();
    }

    public Director getDirectorById(Integer id) {
        return directorDbRepository.getDirectorById(id).orElseThrow(() -> new NotFoundException("Ошибка! Директора с заданным " +
                "идентификатором не существует"));
    }

    public Director addDirector(Director director) {
        log.info("Директор с id {} добавлен.", director.getId());
        return directorDbRepository.addDirector(director);
    }

    public Director updateDirector(Director director) {
        log.debug("Изменение параметров директора с идентификатором {}", director.getId());
        directorDbRepository.getDirectorById(director.getId()).orElseThrow(() ->
                new NotFoundException("Ошибка! Директора с заданным идентификатором не существует"));
        return directorDbRepository.updateDirector(director);
    }

    public void deleteDirector(Integer id) {
        getDirectorById(id);
        log.info("Директор с id {} удалён.", id);
        directorDbRepository.deleteDirector(id);
    }
}
