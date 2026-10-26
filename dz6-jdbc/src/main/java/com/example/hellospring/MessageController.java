package com.example.hellospring;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    /** GET /messages?page=0&size=30[&author=Имя] — новые сверху. */
    @GetMapping
    public PageResponse<Message> getMessages(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "30") int size,
                                             @RequestParam(required = false) String author) {
        return service.getPage(author, page, size);
    }

    @GetMapping("/{id}")
    public Message getMessage(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Message createMessage(@Valid @RequestBody NewMessageRequest request) {
        return service.create(request.content(), request.author());
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Message> createMessages(@RequestBody @NotEmpty List<@Valid NewMessageRequest> requests) {
        return service.createBatch(requests);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long id) {
        service.delete(id);
    }

    public record NewMessageRequest(
            @NotBlank @Size(max = 255) String content,
            @Size(max = 100) String author) {
    }
}
