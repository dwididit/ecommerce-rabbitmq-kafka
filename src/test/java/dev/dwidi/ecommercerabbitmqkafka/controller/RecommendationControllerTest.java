package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaConsumerService;
import dev.dwidi.ecommercerabbitmqkafka.service.kafka.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecommendationControllerTest {

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private Supplier<String> uuidSupplier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(recommendationController)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void recommendProducts_userNotFound() throws Exception {
        String fixedUUID = "ed0f2a92-d154-4cac-9cf1-e2e4a5e42825";

        when(uuidSupplier.get()).thenReturn(fixedUUID);
        doThrow(new UserNotFoundException("User not found")).when(kafkaConsumerService).checkUserExists(anyLong());

        mockMvc.perform(post("/api/recommendations/byUser")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("ed0f2a92-d154-4cac-9cf1-e2e4a5e42825"))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
