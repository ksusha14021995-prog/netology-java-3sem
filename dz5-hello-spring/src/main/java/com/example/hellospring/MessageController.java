package com.example.hellospring;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/messages")
    public List<Message> getMessages() {
        return service.getLatestMessages();
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message createMessage(@Valid @RequestBody NewMessageRequest request) {
        return service.createMessage(request.content(), request.author());
    }

    public record NewMessageRequest(
            @NotBlank @Size(max = 1000) String content,
            @Size(max = 100) String author) {
    }
}
