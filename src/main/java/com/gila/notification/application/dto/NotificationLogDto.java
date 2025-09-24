package com.gila.notification.application.dto;

import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for transferring notification log data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogDto {
    private Long id;
    private Long messageId;
    private String messageContent;
    private Category messageCategory;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
}