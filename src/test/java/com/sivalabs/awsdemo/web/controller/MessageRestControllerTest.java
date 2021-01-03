package com.sivalabs.awsdemo.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.awsdemo.entity.Message;
import com.sivalabs.awsdemo.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageRestController.class)
class MessageRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        doNothing().when(messageService).sendMessage(any(String.class));
    }

    @Test
    void shouldFetchAllMessages() throws Exception {
        given(messageService.getMessages()).willReturn(List.of());

        this.mockMvc.perform(get("/api/messages"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void shouldCreateNewTodo() throws Exception {
        Message message = new Message();
        message.setContent("hello");
        this.mockMvc.perform(post("/api/messages")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteMessage() throws Exception {
        Long msgId = 1L;
        given(messageService.deleteMessage(msgId)).willReturn(true);

        this.mockMvc.perform(delete("/api/messages/{id}", msgId))
            .andExpect(status().isNoContent());
    }
}
