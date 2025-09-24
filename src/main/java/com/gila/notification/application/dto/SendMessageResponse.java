package com.gila.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing the result of a message send operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageResponse {
    private Long messageId;
    private String status;
    private int totalUsersNotified;
    private int successfulNotifications;
    private int failedNotifications;
    private String message;
}