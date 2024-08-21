package ru.yandex.practicum.filmorate.repository.base;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDbRepository<T> implements BaseRepository<T> {

    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected final String tableName;
    protected final String prefix;

    public Optional<T> getById(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.getOne(getSimpleQueryGetById(), params);
    }

    public List<T> getAll() {
        return jdbc.query(getSimpleQueryAllData(), mapper);
    }

    protected Optional<T> getOne(String query, Map<String, Object> params) {
        try {
            T res = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> getMany(String query) {
        return jdbc.query(query, mapper);
    }

    protected List<T> getMany(String query, Map<String, Object> params) {
        return jdbc.query(query, params, mapper);
    }

    private String getSimpleQueryAllData() {
        return "SELECT * FROM " + tableName + ";";
    }

    private String getSimpleQueryGetById() {
        return "SELECT * FROM " + tableName + " WHERE " + prefix + "_id=:id;";
    }

//    protected long insert(String query, MapSqlParameterSource params) {
//        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbc.update(query, params, keyHolder);
//
//        return
//
//        jdbc.update(connection -> {
//            PreparedStatement ps = connection
//                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
//            for (int idx = 0; idx < params.length; idx++) {
//                ps.setObject(idx + 1, params[idx]);
//            }
//            return ps;}, keyHolder);
//
//        Long id = keyHolder.getKeyAs(Long.class);
//
//        // Возвращаем id нового объекта
//        if (id == null) {
//            throw new InternalServerException("Не удалось сохранить данные");
//        }
//
//        return id;
//    }
}
