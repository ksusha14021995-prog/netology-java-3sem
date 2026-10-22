package com.example.hellospring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloSpringApplicationTests {
    private static final String CONTEXT = "/api";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository repository;

    @BeforeEach
    void resetStorage() {
        repository.clear();
        repository.save(new Message("Hello, Spring!", "System"));
        repository.save(new Message("In-memory storage works", "System"));
    }

    // MockMvc не применяет server.servlet.context-path сам — указываем его явно.
    private static MockHttpServletRequestBuilder apiGet(String path) {
        return get(CONTEXT + path).contextPath(CONTEXT);
    }

    private static MockHttpServletRequestBuilder apiPost(String path) {
        return post(CONTEXT + path).contextPath(CONTEXT);
    }

    @Test
    void rootEndpointSaysHello() throws Exception {
        mockMvc.perform(apiGet("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }

    @Test
    void messagesEndpoint() throws Exception {
        mockMvc.perform(apiGet("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello, Spring!"))
                .andExpect(jsonPath("$[1].content").value("In-memory storage works"));
    }

    @Test
    void postCreatesMessageWithAuthor() throws Exception {
        mockMvc.perform(apiPost("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"New message\", \"author\": \"Ksenia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.author").value("Ksenia"))
                .andExpect(jsonPath("$.content").value("New message"));

        mockMvc.perform(apiGet("/messages"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].author").value("Ksenia"));
    }

    @Test
    void postWithoutAuthorFallsBackToAnonymous() throws Exception {
        mockMvc.perform(apiPost("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"No author\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("Anonymous"));
    }

    @Test
    void postWithBlankContentIsRejected() throws Exception {
        mockMvc.perform(apiPost("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"   \", \"author\": \"Ksenia\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReturnsOnlyLatestThirtyMessages() throws Exception {
        for (int i = 1; i <= 40; i++) {
            mockMvc.perform(apiPost("/messages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"content\": \"msg " + i + "\", \"author\": \"bot\"}"))
                    .andExpect(status().isCreated());
        }
        // Всего 42 сообщения: выводятся последние 30 — с "msg 11" по "msg 40", по порядку.
        mockMvc.perform(apiGet("/messages"))
                .andExpect(jsonPath("$", hasSize(30)))
                .andExpect(jsonPath("$[0].content").value("msg 11"))
                .andExpect(jsonPath("$[29].content").value("msg 40"));
    }
}
