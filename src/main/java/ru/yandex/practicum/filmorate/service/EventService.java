package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    public void register(Integer userId,
                         Event.Operation operation,
                         Event.EventType eventType,
                         Integer entityId) {
        Event event = Event.builder()
                .userId(userId)
                .operation(operation)
                .eventType(eventType)
                .entityId(entityId)
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        repository.addEvent(event);
    }

    public List<Event> getByUserId(Integer userId) {
        return repository.getByUserId(userId);
    }

    public Event getByEventId(Integer eventId) {
        return repository.getByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("event with specified event id does not exist"));
    }
}
