package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.base.BaseRepository;
import ru.yandex.practicum.filmorate.repository.rating.RatingDbRepository;

@Slf4j
@Service
public class RatingService extends BaseService<Rating> {
    public RatingService(RatingDbRepository repository) {
        super(repository);
    }
}
