package com.example.hellospring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloSpringApplicationTests {
    private static final String CONTEXT = "/api";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService service;

    @BeforeEach
    void resetDatabase() {
        service.resetWithSampleData();
    }

    // MockMvc не применяет server.servlet.context-path сам — указываем его явно.
    private static MockHttpServletRequestBuilder api(MockHttpServletRequestBuilder builder) {
        return builder.contextPath(CONTEXT);
    }

    private static MockHttpServletRequestBuilder postJson(String path, String json) {
        return api(post(CONTEXT + path).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void getMessagesReturnsInitialData() throws Exception {
        mockMvc.perform(api(get(CONTEXT + "/messages")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].author", notNullValue()))
                .andExpect(jsonPath("$.content[0].content", anyOf(
                        is("Hello, JDBC Spring!"),
                        is("Now using H2 database via JdbcTemplate."))));
    }

    @Test
    void postCreatesMessageAndUser() throws Exception {
        mockMvc.perform(postJson("/messages", "{\"content\": \"New message via POST\", \"author\": \"Tester\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.author", is("Tester")))
                .andExpect(jsonPath("$.content", is("New message via POST")));

        mockMvc.perform(api(get(CONTEXT + "/messages")))
                .andExpect(jsonPath("$.totalElements", is(3)));
        mockMvc.perform(api(get(CONTEXT + "/users")))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void missingAuthorBecomesAnonymous() throws Exception {
        mockMvc.perform(postJson("/messages", "{\"content\": \"no author\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author", is("Anonymous")));
    }

    @Test
    void unknownMessageReturns404() throws Exception {
        mockMvc.perform(api(get(CONTEXT + "/messages/999999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
        mockMvc.perform(api(delete(CONTEXT + "/messages/999999")))
                .andExpect(status().isNotFound());
        mockMvc.perform(api(get(CONTEXT + "/users/999999/messages")))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidInputReturns400() throws Exception {
        mockMvc.perform(postJson("/messages", "{\"content\": \"  \"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(api(get(CONTEXT + "/messages").param("size", "0")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(api(get(CONTEXT + "/messages").param("page", "-1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void batchCreatesAllMessagesAndPaginationWorks() throws Exception {
        StringBuilder json = new StringBuilder("[");
        for (int i = 1; i <= 45; i++) {
            json.append(i > 1 ? "," : "")
                    .append("{\"content\": \"batch ").append(i).append("\", \"author\": \"")
                    .append(i % 2 == 0 ? "Even" : "Odd").append("\"}");
        }
        json.append("]");

        mockMvc.perform(postJson("/messages/batch", json.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(45)))
                .andExpect(jsonPath("$[0].content", is("batch 1")))
                .andExpect(jsonPath("$[44].content", is("batch 45")));

        // 47 сообщений, по 20 на страницу -> 3 страницы, на последней 7
        mockMvc.perform(api(get(CONTEXT + "/messages").param("page", "2").param("size", "20")))
                .andExpect(jsonPath("$.totalElements", is(47)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.content", hasSize(7)));

        // новые сверху: первая запись первой страницы — последняя из батча
        mockMvc.perform(api(get(CONTEXT + "/messages").param("size", "5")))
                .andExpect(jsonPath("$.content[0].content", is("batch 45")));
    }

    @Test
    void searchByAuthor() throws Exception {
        mockMvc.perform(postJson("/messages/batch",
                        "[{\"content\": \"a1\", \"author\": \"Ksenia\"}, {\"content\": \"b1\", \"author\": \"Bob\"}," +
                        " {\"content\": \"a2\", \"author\": \"Ksenia\"}]"))
                .andExpect(status().isCreated());

        mockMvc.perform(api(get(CONTEXT + "/messages").param("author", "Ksenia")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].content", is("a2")))
                .andExpect(jsonPath("$.content[1].content", is("a1")));
    }

    @Test
    void userEndpoints() throws Exception {
        mockMvc.perform(postJson("/users", "{\"name\": \"Alice\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alice")));
        mockMvc.perform(postJson("/users", "{\"name\": \"Alice\"}"))
                .andExpect(status().isBadRequest());

        String body = mockMvc.perform(postJson("/messages", "{\"content\": \"hi\", \"author\": \"Alice\"}"))
                .andReturn().getResponse().getContentAsString();
        long userId = Long.parseLong(body.replaceAll(".*\"userId\":(\\d+).*", "$1"));

        mockMvc.perform(api(get(CONTEXT + "/users/" + userId + "/messages")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].author", is("Alice")));
    }

    @Test
    void deleteRemovesMessage() throws Exception {
        String body = mockMvc.perform(postJson("/messages", "{\"content\": \"to delete\"}"))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(body.replaceAll("^\\{\"id\":(\\d+).*", "$1"));

        mockMvc.perform(api(delete(CONTEXT + "/messages/" + id))).andExpect(status().isNoContent());
        mockMvc.perform(api(get(CONTEXT + "/messages/" + id))).andExpect(status().isNotFound());
    }
}
