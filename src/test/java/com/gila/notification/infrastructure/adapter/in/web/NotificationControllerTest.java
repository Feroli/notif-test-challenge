package com.gila.notification.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gila.notification.application.dto.SendMessageRequest;
import com.gila.notification.application.mapper.NotificationMapper;
import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.port.in.GetNotificationLogsUseCase;
import com.gila.notification.domain.port.in.SendMessageUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendMessageUseCase sendMessageUseCase;

    @MockBean
    private GetNotificationLogsUseCase getNotificationLogsUseCase;

    @MockBean
    private NotificationMapper mapper;

    @Test
    @DisplayName("Should send message successfully")
    void sendMessage_WithValidRequest_ReturnsSuccess() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("Test message");

        SendMessageUseCase.SendMessageResult result = new SendMessageUseCase.SendMessageResult(
                1L, 5, 4, 1
        );

        when(sendMessageUseCase.sendMessage(any())).thenReturn(result);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.messageId").value(1))
                .andExpect(jsonPath("$.totalUsersNotified").value(5))
                .andExpect(jsonPath("$.successfulNotifications").value(4))
                .andExpect(jsonPath("$.failedNotifications").value(1));
    }

    @Test
    @DisplayName("Should return bad request for empty message")
    void sendMessage_WithEmptyMessage_ReturnsBadRequest() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setCategory(Category.SPORTS);
        request.setMessage("");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request for null category")
    void sendMessage_WithNullCategory_ReturnsBadRequest() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setCategory(null);
        request.setMessage("Test message");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all notification logs")
    void getNotificationLogs_ReturnsAllLogs() throws Exception {
        when(getNotificationLogsUseCase.getAllLogs()).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get notification logs by user ID")
    void getNotificationLogsByUser_ReturnsUserLogs() throws Exception {
        Long userId = 1L;
        when(getNotificationLogsUseCase.getLogsByUserId(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/logs/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get notification logs by message ID")
    void getNotificationLogsByMessage_ReturnsMessageLogs() throws Exception {
        Long messageId = 1L;
        when(getNotificationLogsUseCase.getLogsByMessageId(messageId)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/logs/message/{messageId}", messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get all categories")
    void getCategories_ReturnsAllCategories() throws Exception {
        mockMvc.perform(get("/api/notifications/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Sports"))
                .andExpect(jsonPath("$[1]").value("Finance"))
                .andExpect(jsonPath("$[2]").value("Movies"));
    }
}