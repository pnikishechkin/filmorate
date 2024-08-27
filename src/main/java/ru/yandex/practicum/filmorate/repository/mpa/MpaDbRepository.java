package ru.yandex.practicum.filmorate.repository.mpa;

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
public class MpaDbRepository extends BaseDbRepository<Mpa> implements MpaRepository {

    public MpaDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    private static final String SQL_GET_ALL_MPAs =
            "SELECT * FROM mpa;";

    private static final String SQL_GET_MPA_BY_ID =
            "SELECT * FROM mpa WHERE mpa_id=:id;";

    private static final String SQL_INSERT_MPA =
            "INSERT INTO mpa (mpa_name) VALUES (:mpa_name);";

    @Override
    public List<Mpa> getAll() {
        return getMany(SQL_GET_ALL_MPAs);
    }

    @Override
    public Optional<Mpa> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_MPA_BY_ID, params);
    }

    @Override
    public Mpa addRating(Mpa mpa) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        System.out.println(mpa.getName());

        params.addValue("mpa_name", mpa.getName());

        jdbc.update(SQL_INSERT_MPA, params, keyHolder);
        mpa.setId(keyHolder.getKeyAs(Integer.class));
        return mpa;
    }
}
