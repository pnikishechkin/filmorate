package ru.yandex.practicum.filmorate.repository.rating;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class RatingDbRepository extends BaseDbRepository<Mpa> {

    public RatingDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    private static final String SQL_GET_ALL_RATINGS =
            "SELECT * FROM ratings;";

    private static final String SQL_GET_RATINGS_BY_ID =
            "SELECT * FROM ratings WHERE rating_id=:id;";

    private static final String SQL_INSERT_RATING =
            "INSERT INTO ratings (rating_name) VALUES (:rating_name);";

    public List<Mpa> getAll() {
        return getMany(SQL_GET_ALL_RATINGS);
    }

    public Optional<Mpa> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_RATINGS_BY_ID, params);
    }

    public Mpa addRating(Mpa rating) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        System.out.println(rating.getName());

        params.addValue("rating_name", rating.getName());

        jdbc.update(SQL_INSERT_RATING, params, keyHolder);
        rating.setId(keyHolder.getKeyAs(Integer.class));
        return rating;
    }
}
