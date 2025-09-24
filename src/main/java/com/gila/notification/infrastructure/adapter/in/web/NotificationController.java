package com.gila.notification.infrastructure.adapter.in.web;

import com.gila.notification.application.dto.NotificationLogDto;
import com.gila.notification.application.dto.SendMessageRequest;
import com.gila.notification.application.dto.SendMessageResponse;
import com.gila.notification.application.mapper.NotificationMapper;
import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.port.in.GetNotificationLogsUseCase;
import com.gila.notification.domain.port.in.SendMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for notification operations.
 * Provides endpoints for sending messages and retrieving notification logs.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_ERROR = "ERROR";
    private static final String SUCCESS_MESSAGE_TEMPLATE = "Message sent successfully. %d successful, %d failed notifications.";
    private static final String GENERIC_ERROR_MESSAGE = "An error occurred while processing your request";

    private final SendMessageUseCase sendMessageUseCase;
    private final GetNotificationLogsUseCase getNotificationLogsUseCase;
    private final NotificationMapper mapper;

    @PostMapping("/send")
    public ResponseEntity<SendMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("Received message request for category: {}", request.getCategory());

        try {
            SendMessageUseCase.SendMessageCommand command = new SendMessageUseCase.SendMessageCommand(
                    request.getCategory(),
                    request.getMessage()
            );

            SendMessageUseCase.SendMessageResult result = sendMessageUseCase.sendMessage(command);

            SendMessageResponse response = SendMessageResponse.builder()
                    .messageId(result.messageId())
                    .status(STATUS_SUCCESS)
                    .totalUsersNotified(result.totalUsers())
                    .successfulNotifications(result.successfulNotifications())
                    .failedNotifications(result.failedNotifications())
                    .message(String.format(SUCCESS_MESSAGE_TEMPLATE,
                            result.successfulNotifications(), result.failedNotifications()))
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            SendMessageResponse errorResponse = SendMessageResponse.builder()
                    .status(STATUS_ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error sending message", e);
            SendMessageResponse errorResponse = SendMessageResponse.builder()
                    .status(STATUS_ERROR)
                    .message(GENERIC_ERROR_MESSAGE)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Gets notification logs with optional filtering.
     *
     * @param userId optional user ID filter
     * @param status optional status filter
     * @param channel optional channel filter
     * @param category optional category filter
     * @param page page number (0-based)
     * @param size page size
     * @return filtered and paginated logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<NotificationLogDto>> getNotificationLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching notification logs with filters - userId: {}, status: {}, channel: {}, category: {}",
                userId, status, channel, category);

        List<NotificationLogDto> logs = getNotificationLogsUseCase.getAllLogs()
                .stream()
                .filter(log -> userId == null || log.getUserId().equals(userId))
                .filter(log -> status == null || log.getStatus().toString().equals(status))
                .filter(log -> channel == null || log.getChannel().toString().equals(channel))
                .filter(log -> category == null || log.getMessageCategory().toString().equals(category))
                .skip(page * size)
                .limit(size)
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<NotificationLogDto>> getNotificationLogsByUser(@PathVariable Long userId) {
        log.info("Fetching notification logs for user: {}", userId);
        List<NotificationLogDto> logs = getNotificationLogsUseCase.getLogsByUserId(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/message/{messageId}")
    public ResponseEntity<List<NotificationLogDto>> getNotificationLogsByMessage(@PathVariable Long messageId) {
        log.info("Fetching notification logs for message: {}", messageId);
        List<NotificationLogDto> logs = getNotificationLogsUseCase.getLogsByMessageId(messageId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
}