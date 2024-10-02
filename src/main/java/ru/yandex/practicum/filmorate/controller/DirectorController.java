package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

/**
 * Контроллер для реализации API методов, связанных с режиссерами
 */
@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DirectorController {
    private final DirectorService directorService;

    /**
     * Получить всех режиссеров
     *
     * @return список всех режиссеров
     */
    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }

    /**
     * Получить режиссера по идентификатору
     *
     * @param id идентификатор режиссера
     * @return опционально - объект режиссер
     */
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable final Integer id) {
        return directorService.getDirectorById(id);
    }

    /**
     * Добавить режиссера
     *
     * @param director объект добавляемого режиссера
     * @return объект добавленного режиссера
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    /**
     * Удалить режиссера
     *
     * @param id идентификатор удаляемого режиссера
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable @Positive final Integer id) {
        directorService.deleteDirector(id);
    }

    /**
     * Редактирование режиссера
     *
     * @param director объект изменяемого режиссера
     * @return объект измененного режиссера
     */
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }
}
