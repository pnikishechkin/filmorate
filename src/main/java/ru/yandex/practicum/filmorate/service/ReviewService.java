package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewDbRepository;
import ru.yandex.practicum.filmorate.repository.user.UserDbRepository;

import java.util.List;

/**
 * Сервисный класс для управления отзывами на фильмы
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class ReviewService {

    private final ReviewDbRepository reviewDbRepository;
    private final FilmDbRepository filmDbRepository;
    private final UserDbRepository userDbRepository;

    public Review addReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        return reviewDbRepository.addReview(review);
    }

    public Review updateReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        checkReviewExist(review.getReviewId());
        return reviewDbRepository.updateReview(review);
    }

    public Boolean deleteReview(Integer id) {
        return reviewDbRepository.deleteReview(id);
    }

    public Review getReviewById(Integer id) {
        return reviewDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Отзыва с заданным " +
                "идентификатором не существует"));
    }

    public List<Review> getReviewsByFilm(Integer filmId, Integer count) {

        if (filmId == null) {
            return reviewDbRepository.getAll(count);
        } else {
            checkFilmExist(filmId);
            return reviewDbRepository.getByFilmId(filmId, count);
        }
    }

    public void setLike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, true);
    }

    public void setDislike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, false);
    }

    public void deleteLike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.deleteLike(id, userId);
    }

    private void checkReviewExist(Integer reviewId) {
        reviewDbRepository.getById(reviewId).orElseThrow(
                () -> new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    private void checkUserExist(Integer userId) {
        userDbRepository.getById(userId).orElseThrow(
                () -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
    }

    private void checkFilmExist(Integer filmId) {
        filmDbRepository.getById(filmId).orElseThrow(
                () -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
    }

}