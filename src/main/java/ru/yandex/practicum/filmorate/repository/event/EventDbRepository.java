package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.base.BaseDbRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для управления событиями
 */
@Repository
public class EventDbRepository extends BaseDbRepository<Event> implements EventRepository {
    private static final String SQL_INSERT_EVENT =
            "INSERT INTO events (user_id, event_type, operation, entity_id, timestamp) " +
                    "VALUES (:user_id, :event_type, :operation, :entity_id, :timestamp);";

    private static final String SQL_GET_EVENT_BY_USER_ID =
            "SELECT * FROM events WHERE user_id = :user_id";

    private static final String SQL_GET_EVENT_BY_EVENT_ID =
            "SELECT * FROM events WHERE event_id = :event_id";

    public EventDbRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Добавить событие
     *
     * @param event объект добавляемого события
     * @return объект добавленного события
     */
    @Override
    public Event addEvent(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", event.getUserId());
        params.addValue("event_type", event.getEventType().name());
        params.addValue("operation", event.getOperation().name());
        params.addValue("entity_id", event.getEntityId());
        params.addValue("timestamp", event.getTimestamp());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(
                SQL_INSERT_EVENT,
                params,
                keyHolder
        );

        event.setEventId(keyHolder.getKeyAs(Integer.class));

        return event;
    }

    /**
     * Получить событие по идентификатору пользователя
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public List<Event> getByUserId(Integer userId) {
        return getMany(
                SQL_GET_EVENT_BY_USER_ID,
                Map.of("user_id", userId)
        );
    }

    /**
     * Получить событие по идентификатору
     *
     * @param eventId идентификатор события
     */
    @Override
    public Optional<Event> getByEventId(Integer eventId) {
        return getOne(
                SQL_GET_EVENT_BY_EVENT_ID,
                Map.of("event_id", eventId)
        );
    }
}
