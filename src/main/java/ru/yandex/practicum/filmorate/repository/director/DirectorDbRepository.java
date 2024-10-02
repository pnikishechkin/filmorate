package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.*;

/**
 * Репозиторий для управления режиссерами
 */
@Repository
public class DirectorDbRepository extends BaseDbRepository<Director> implements DirectorRepository {

    public DirectorDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    private static final String SQL_GET_ALL_DIRECTORS =
            "SELECT * FROM directors;";

    private static final String SQL_GET_DIRECTOR_BY_ID =
            "SELECT * FROM directors WHERE director_id=:director_id;";

    private static final String SQL_INSERT_DIRECTOR =
            "INSERT INTO directors (director_name) " +
                    "VALUES (:director_name);";

    private static final String SQL_UPDATE_DIRECTOR =
            "UPDATE directors SET director_name=:director_name WHERE director_id=:director_id;";


    private static final String SQL_DELETE_DIRECTOR =
            "DELETE FROM directors WHERE director_id=:director_id";

    /**
     * Получить всех режиссеров
     *
     * @return список всех режиссеров
     */
    @Override
    public List<Director> getAll() {
        return getMany(SQL_GET_ALL_DIRECTORS);
    }

    /**
     * Получить режиссера по идентификатору
     *
     * @param id идентификатор режиссера
     * @return опционально - объект режиссер
     */
    @Override
    public Optional<Director> getDirectorById(Integer id) {
        Map<String, Object> params = Map.of("director_id", id);
        Optional<Director> director = getOne(SQL_GET_DIRECTOR_BY_ID, params);
        return director;
    }

    /**
     * Добавить режиссера
     *
     * @param director объект добавляемого режиссера
     * @return объект добавленного режиссера
     */
    @Override
    public Director addDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("director_name", director.getName());

        jdbc.update(SQL_INSERT_DIRECTOR, params, keyHolder);
        director.setId(keyHolder.getKeyAs(Integer.class));
        return director;
    }

    /**
     * Удалить режиссера
     *
     * @param id идентификатор удаляемого режиссера
     */
    @Override
    public void deleteDirector(Integer id) {
        Map<String, Object> params = Map.of("director_id", id);
        jdbc.update(SQL_DELETE_DIRECTOR, params);
    }

    /**
     * Редактирование режиссера
     *
     * @param director объект изменяемого режиссера
     * @return объект измененного режиссера
     */
    @Override
    public Director updateDirector(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", director.getId());

        // Обновить данные директора
        params = new MapSqlParameterSource();
        params.addValue("director_id", director.getId());
        params.addValue("director_name", director.getName());

        // Обновление записи
        jdbc.update(SQL_UPDATE_DIRECTOR, params);

        // Получение обновленного полного объекта из базы (необходимо для наполнения внутренних объектов)
        return getDirectorById(director.getId()).get();
    }
}
