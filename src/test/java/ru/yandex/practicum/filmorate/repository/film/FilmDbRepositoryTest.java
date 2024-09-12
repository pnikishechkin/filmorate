package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.director.DirectorDbRepository;
import ru.yandex.practicum.filmorate.repository.director.DirectorRowMapper;
import ru.yandex.practicum.filmorate.repository.genre.GenreDbRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaDbRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@JdbcTest
@Import({FilmDbRepository.class, FilmExtractor.class, FilmGenreRowMapper.class, FilmRowMapper.class,
        GenreDbRepository.class, GenreRowMapper.class, MpaDbRepository.class, MpaRowMapper.class,
        DirectorDbRepository.class, DirectorRowMapper.class, FilmDirectorRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("FilmDbRepositoryTest")
class FilmDbRepositoryTest {

    private final FilmDbRepository filmDbRepository;
    private final MpaDbRepository mpaDbRepository;

    public static final Integer COUNT_FILMS = 3;
    public static final Integer FIRST_FILM_ID = 1;

    public static Film getFirstFilm() {
        return Film.builder().id(FIRST_FILM_ID)
                .name("Терминатор")
                .description("Большой железный")
                .releaseDate(LocalDate.of(1990, 05, 19))
                .duration(90)
                .build();
    }

    public Film getNewFilm() {
        Optional<Mpa> mpa = mpaDbRepository.getById(1);
        return Film.builder()
                .name("Новый фильм")
                .description("Совсем новый")
                .mpa(mpa.get())
                .releaseDate(LocalDate.of(2024, 8, 28))
                .duration(190)
                .build();
    }

    @Test
    @DisplayName("Тест получения всех фильмов")
    void getAll_ReturnAllFilms() {
        // when
        List<Film> films = filmDbRepository.getAll();

        // then
        Assertions.assertEquals(COUNT_FILMS, films.size());
        Assertions.assertEquals(getFirstFilm(), films.get(0));
    }

    @Test
    @DisplayName("Тест получения имеющегося фильма по идентификатору")
    void getById_FilmExist_ReturnFilm() {
        // when
        Optional<Film> film = filmDbRepository.getById(FIRST_FILM_ID);

        // then
        Assertions.assertTrue(film.isPresent());
        Assertions.assertEquals(getFirstFilm(), film.get());
    }

    @Test
    @DisplayName("Тест добавления нового фильма с корректными параметрами")
    void addFilm_CorrectParams_FilmAdded() {

        // init
        Film newFilm = getNewFilm();

        // when
        newFilm = filmDbRepository.addFilm(newFilm);

        // then
        List<Film> films = filmDbRepository.getAll();
        Assertions.assertEquals(COUNT_FILMS + 1, films.size());

        Optional<Film> film = filmDbRepository.getById(newFilm.getId());
        Assertions.assertTrue(film.isPresent());
        Assertions.assertEquals(newFilm, film.get());
    }

    @Test
    @DisplayName("Тест обновления имеющегося фильма с корректными параметрами")
    void updateFilm_CorrectParams_FilmUpdated() {
        // init
        Film film = filmDbRepository.getById(FIRST_FILM_ID).get();
        String updName = "Обновленный фильм";
        String updDescription = "Обновленный фильм";
        LocalDate updDate = LocalDate.of(1990, 5, 5);
        Integer updDur = 45;

        // when
        film.setName(updName);
        film.setDescription(updDescription);
        film.setReleaseDate(updDate);
        film.setDuration(updDur);
        filmDbRepository.updateFilm(film);

        // then
        Film updFilm = filmDbRepository.getById(FIRST_FILM_ID).get();
        Assertions.assertEquals(updName, updFilm.getName());
        Assertions.assertEquals(updDescription, updFilm.getDescription());
        Assertions.assertEquals(updDur, updFilm.getDuration());
        Assertions.assertEquals(updDate, updFilm.getReleaseDate());
    }

    @Test
    @DisplayName("Тест добавления лайка на фильм")
    void adduserLike_LikeAdded() {
        // init
        Film film = filmDbRepository.getById(2).get();

        // when
        filmDbRepository.addUserLike(2, 3);

        // then
        Assertions.assertTrue(filmDbRepository.getLikeFilmsByUserId(3).contains(film));
    }

    @Test
    @DisplayName("Тест удаления лайка с фильма")
    void deleteUserLike_LikeDeleted() {

        // init
        Film film = filmDbRepository.getById(1).get();
        Assertions.assertTrue(filmDbRepository.getLikeFilmsByUserId(1).contains(film));

        // when
        filmDbRepository.deleteUserLike(1, 1);

        // then
        Assertions.assertFalse(filmDbRepository.getLikeFilmsByUserId(1).contains(film));
    }

    @Test
    @DisplayName("Тест получения списка фильмов, которым указанный пользователь поставил лайк")
    void getLikeFilmsByUserId_CorrectCountLikes() {

        // init
        Film film1 = filmDbRepository.getById(1).get();
        Film film2 = filmDbRepository.getById(2).get();
        Film film3 = filmDbRepository.getById(3).get();

        // when
        Set<Film> films = filmDbRepository.getLikeFilmsByUserId(1);

        // then
        Assertions.assertEquals(3, films.size());
        Assertions.assertTrue(films.contains(film1));
        Assertions.assertTrue(films.contains(film1));
        Assertions.assertTrue(films.contains(film1));
    }

    @Test
    @DisplayName("Тест получения списка популярных фильмов")
    void getPopularFilms_ReturnCorrectList() {

        // init
        Film film1 = filmDbRepository.getById(1).get();
        Film film2 = filmDbRepository.getById(2).get();
        Film film3 = filmDbRepository.getById(3).get();

        // when
        List<Film> films = filmDbRepository.getPopularFilms(5, 1, 2000);

        // then
        Assertions.assertEquals(film1, films.get(0));
        Assertions.assertEquals(film2, films.get(1));
        Assertions.assertEquals(film3, films.get(2));
    }

    @Test
    @DisplayName("Удаление имеющегося в базе фильма")
    void deleteExistFilm_FilmDeleted() {
        // when
        Boolean res = filmDbRepository.deleteFilm(FIRST_FILM_ID);

        // then
        Assertions.assertTrue(res);
        Assertions.assertEquals(2, filmDbRepository.getAll().size());
        Assertions.assertFalse(filmDbRepository.getAll().contains(getFirstFilm()));
    }

    @Test
    @DisplayName("Удаление фильма, которого нет в базе")
    void deleteNotExistFilm_ResultFalse() {
        // when (идентификатор не существующего фильма)
        Boolean res = filmDbRepository.deleteFilm(100);

        // then
        Assertions.assertFalse(res);
        Assertions.assertEquals(3, filmDbRepository.getAll().size());
    }

}