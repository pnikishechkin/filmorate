package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.rating.RatingDbRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingDbRepository ratingDbRepository;

    public List<Mpa> getRatings() {
        return ratingDbRepository.getAll();
    }

    public Mpa getRatingById(Integer id) {
        return ratingDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Рейтинга с заданным " +
                "идентификатором не существует"));
    }

    public Mpa addRating(Mpa rating) {
        return ratingDbRepository.addRating(rating);
    }
}
