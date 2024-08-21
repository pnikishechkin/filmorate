package ru.yandex.practicum.filmorate.repository.rating;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class RatingDbRepository extends BaseDbRepository<Rating> {

    public RatingDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper, "ratings", "rating");
    }

    private static final String SQL_GET_ALL_RATINGS =
            "select * from RATINGS;";

    private static final String SQL_GET_RATINGS_BY_ID =
            "select * from RATINGS WHERE rating_id=:id;";

    @Override
    public List<Rating> getAll() {
        return getMany(SQL_GET_ALL_RATINGS);
    }

    @Override
    public Optional<Rating> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_RATINGS_BY_ID, params);
    }
}
