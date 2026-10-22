package com.example.hellospring;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MessageRepository {
    private final ConcurrentHashMap<Long, Message> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(idCounter.incrementAndGet());
        }
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(Instant.now());
        }
        storage.put(message.getId(), message);
        return message;
    }

    public Message findById(Long id) {
        return storage.get(id);
    }

    public Iterable<Message> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();
    }

    /** Последние {@code limit} сообщений в хронологическом порядке (id растёт монотонно). */
    public List<Message> findLatest(int limit) {
        return storage.values().stream()
                .sorted(Comparator.comparing(Message::getId).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(Message::getId))
                .toList();
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public void clear() {
        storage.clear();
        idCounter.set(0);
    }
}
