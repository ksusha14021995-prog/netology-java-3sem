package com.example.hellospring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    static final String DEFAULT_AUTHOR = "Anonymous";
    static final int MAX_PAGE_SIZE = 100;
    static final int MAX_BATCH_SIZE = 1000;

    private final JdbcMessageRepository messages;
    private final JdbcUserRepository users;

    public MessageService(JdbcMessageRepository messages, JdbcUserRepository users) {
        this.messages = messages;
        this.users = users;
    }

    @Transactional
    public void resetWithSampleData() {
        messages.clear();
        users.clear();
        create("Hello, JDBC Spring!", "System");
        create("Now using H2 database via JdbcTemplate.", "Admin");
    }

    public PageResponse<Message> getPage(String author, int page, int size) {
        validatePaging(page, size);
        if (author == null || author.isBlank()) {
            return PageResponse.of(messages.findPage(page, size), page, size, messages.count());
        }
        String name = author.trim();
        return PageResponse.of(messages.findPageByAuthor(name, page, size), page, size, messages.countByAuthor(name));
    }

    public Message getById(Long id) {
        return messages.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с id=" + id + " не найдено"));
    }

    @Transactional
    public Message create(String content, String author) {
        User user = findOrCreateUser(author);
        return messages.save(content.trim(), user.id());
    }

    @Transactional
    public List<Message> createBatch(List<MessageController.NewMessageRequest> requests) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Пустой список сообщений");
        }
        if (requests.size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Не больше " + MAX_BATCH_SIZE + " сообщений за раз");
        }
        Map<String, Long> userIds = new HashMap<>();
        List<Map<String, Object>> rows = requests.stream()
                .map(request -> {
                    String name = normalizeAuthor(request.author());
                    Long userId = userIds.computeIfAbsent(name, n -> findOrCreateUser(n).id());
                    return Map.<String, Object>of("content", request.content().trim(), "userId", userId);
                })
                .toList();
        return messages.findByIds(messages.saveAll(rows));
    }

    @Transactional
    public void delete(Long id) {
        if (messages.deleteById(id) == 0) {
            throw new NotFoundException("Сообщение с id=" + id + " не найдено");
        }
    }

    public List<User> getUsers() {
        return users.findAll();
    }

    public User getUser(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Transactional
    public User createUser(String name) {
        String normalized = name.trim();
        if (users.findByName(normalized).isPresent()) {
            throw new IllegalArgumentException("Пользователь '" + normalized + "' уже существует");
        }
        return users.save(normalized);
    }

    public PageResponse<Message> getUserMessages(Long userId, int page, int size) {
        return getPage(getUser(userId).name(), page, size);
    }

    private User findOrCreateUser(String author) {
        String name = normalizeAuthor(author);
        return users.findByName(name).orElseGet(() -> users.save(name));
    }

    private static String normalizeAuthor(String author) {
        return (author == null || author.isBlank()) ? DEFAULT_AUTHOR : author.trim();
    }

    private static void validatePaging(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page должен быть >= 0");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size должен быть от 1 до " + MAX_PAGE_SIZE);
        }
    }
}
