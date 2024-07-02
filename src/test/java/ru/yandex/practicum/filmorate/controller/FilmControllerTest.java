package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collection;


@SpringBootTest
public class FilmControllerTest {

    @Autowired
    FilmController controller;

    @Test
    public void addCorrectFilms_getFilms_correctData() {

        Film film =
                Film.builder().id(0).name("Фильм")
                        .description("Описание").duration(30)
                        .releaseDate(LocalDate.of(2005, 12, 10)).build();
        Film newFilm = controller.addFilm(film);

        film = Film.builder().id(1).name("Фильм 2")
                .description("Описание 2").duration(50)
                .releaseDate(LocalDate.of(2003, 12, 10)).build();
        Film newFilm2 = controller.addFilm(film);

        Collection<Film> films = this.controller.getFilms();

        Assertions.assertNotNull(newFilm);
        Assertions.assertNotNull(newFilm2);
        Assertions.assertEquals(2, films.size());
    }

    @Test
    public void addFilmEmptyName_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            Film film =
                    Film.builder().id(0)
                            .description("Описание").duration(30)
                            .releaseDate(LocalDate.of(2005, 12, 10)).build();
            controller.addFilm(film);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Название фильма не может быть пустым");
    }

    @Test
    public void addFilmLongDescription_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            Film film =
                    Film.builder().id(0).name("Название")
                            .description(
                                    "12345678901234567890123456789012345678901234567890123456789012345678901234567" +
                                            "8901234567890123456789012345678901234567890123456789012345678901234567890123456" +
                                            "789012345678901234567890123456789012345678901234567890").duration(30)
                            .releaseDate(LocalDate.of(2005, 12, 10)).build();
            controller.addFilm(film);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Максимальная длина описания фильма — 200 символов");
    }

    @Test
    public void addFilmIncorrectReleaseDate_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            Film film =
                    Film.builder().id(0).name("Название")
                            .description("Описание").duration(30)
                            .releaseDate(LocalDate.of(1800, 12, 10)).build();
            controller.addFilm(film);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Дата релиза фильма не может быть раньше 28 декабря 1895 года");
    }

    @Test
    public void addFilmIncorrectDuration_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            Film film =
                    Film.builder().id(0).name("Название")
                            .description("Описание").duration(-10)
                            .releaseDate(LocalDate.of(2000, 12, 10)).build();
            controller.addFilm(film);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Продолжительность фильма должна быть положительным числом");
    }
}
