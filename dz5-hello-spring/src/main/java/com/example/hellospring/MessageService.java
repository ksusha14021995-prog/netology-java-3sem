package com.example.hellospring;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    static final int MESSAGE_LIMIT = 30;
    static final String DEFAULT_AUTHOR = "Anonymous";

    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
        initializeSampleData();
    }

    private void initializeSampleData() {
        repository.save(new Message("Hello, Spring!", "System"));
        repository.save(new Message("In-memory storage works", "System"));
    }

    public Iterable<Message> getAllMessages() {
        return repository.findAll();
    }

    public List<Message> getLatestMessages() {
        return repository.findLatest(MESSAGE_LIMIT);
    }

    public Message createMessage(String content, String author) {
        String normalizedAuthor = (author == null || author.isBlank()) ? DEFAULT_AUTHOR : author.trim();
        return repository.save(new Message(content.trim(), normalizedAuthor));
    }
}
