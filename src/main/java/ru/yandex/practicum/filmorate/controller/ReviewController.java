package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * Контроллер для реализации API методов, связанных с фильмами
 */
@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Добавление нового отзыва
     *
     * @param review
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review newReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    /**
     * Редактирование уже имеющегося отзыва
     *
     * @param review
     * @return
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удаление уже имеющегося отзыва
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deleteReview(@Positive @PathVariable Integer id) {
        return reviewService.deleteReview(id);
    }

    /**
     * Получение отзыва по идентификатору
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Review getReview(@Positive @PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    /**
     * Получение всех отзывов по идентификатору фильма
     *
     * @param filmId идентификтор фильма. Если не указан, получаем по всем фильмам
     * @param count  количество отзывов. Если не указано, то выводится 10 шт.
     * @return
     */
    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(value = "filmId", required = false) Integer filmId,
                                         @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return reviewService.getReviewsByFilm(filmId, count);
    }

    /**
     * Пользователь ставит лайк отзыву
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void setLike(@Positive @PathVariable Integer id,
                        @Positive @PathVariable Integer userId) {
        reviewService.setLike(id, userId);
    }

    /**
     * Пользователь ставит дизлайк отзыву
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void setDislike(@Positive @PathVariable Integer id,
                           @Positive @PathVariable Integer userId) {
        reviewService.setDislike(id, userId);
    }

    /**
     * Пользователь удаляет лайк/дизлайк отзыву
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive @PathVariable Integer id,
                           @Positive @PathVariable Integer userId) {
        reviewService.deleteLike(id, userId);
    }
}
