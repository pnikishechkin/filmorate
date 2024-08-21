package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmDbRepository;
import ru.yandex.practicum.filmorate.repository.rating.RatingDbRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbRepository filmDbRepository;
    private final RatingDbRepository ratingDbRepository;

    public List<Film> getFilms() {
        return filmDbRepository.getAll();
    }

    public Film getFilmById(Integer id) {
        return filmDbRepository.getById(id).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным " +
                "идентификатором не существует"));
    }

    public Film addFilm(Film film) {
        if (ratingDbRepository.getById(film.getRating().getId()).isPresent()) {
            throw new NotFoundException("Ошибка! Рейтинга с заданным идентификатором не существует");
        }
        // this.checkFilm(film);
        return filmDbRepository.addFilm(film);
    }

/*
    public Film editFilm(Film film) {
        log.debug("Изменение параметров фильма с идентификатором {}", film.getId());
        Film res = filmRepository.editFilm(film).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным " +
                "идентификатором не существует"));
        // final Film f = filmRepository.getFilmById(id).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
        // final Rating rating = film.getRating().getId
        // List<Integer> genreIds = film.getGenres.stream().map(Genre::getId).toList(); // orElseThrow
        // List<Genres> genres = genreRepository.getGenresByIds(genreIds); // orElseThrow

//        f.setName(film.getName());
//        f.setDescription(film.getDescription());
//        f.setDuration(film.getDuration());
//        f.setReleaseDate(film.getReleaseDate());
//        f.setRatingId();
//        f.setGenreIds();
//        f.setUsersIdLike(); // у пользователя лучше хранить список фильмов понравившихся

        //filmRepository.editFilm(f);

        checkFilm(res);
        return res;
    }

    public void addUserLike(Integer filmId, Integer userId) {
        Film film = filmRepository.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с " +
                "заданным идентификатором не существует"));
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
        //film.getUsersIdLike().add(userId);
    }

    public void deleteUserLike(Integer filmId, Integer userId) {

        Film film = filmRepository.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Ошибка! Фильма с " +
                "заданным идентификатором не существует"));
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с " +
                "заданным идентификатором не существует"));
        //film.getUsersIdLike().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return null;
//        return filmRepository.getFilms()
//                .stream().sorted(Comparator.comparingInt(f -> f.getUsersIdLike().size()))
//                .limit(count).collect(Collectors.toList()).reversed();
    }

    private void checkFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Ошибка! Название фильма не может быть пустым");
            throw new ValidationException("Ошибка! Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка! Максимальная длина описания фильма — 200 символов");
            throw new ValidationException("Ошибка! Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка! Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Ошибка! Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Ошибка! Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Ошибка! Продолжительность фильма должна быть положительным числом");
        }
    }
*/
}
