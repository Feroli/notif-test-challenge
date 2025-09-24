package com.gila.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing a notification delivery log.
 * Records the outcome of notification sending attempts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {
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

    /**
     * Creates a success log entry for a delivered notification.
     *
     * @param message the message that was sent
     * @param user the recipient user
     * @param channel the notification channel used
     * @return a new success NotificationLog
     */
    public static NotificationLog createSuccessLog(Message message, User user, NotificationChannel channel) {
        return NotificationLog.builder()
                .messageId(message.getId())
                .messageContent(message.getContent())
                .messageCategory(message.getCategory())
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userPhone(user.getPhoneNumber())
                .channel(channel)
                .status(NotificationStatus.SUCCESS)
                .sentAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a failure log entry for a failed notification.
     *
     * @param message the message that failed to send
     * @param user the intended recipient
     * @param channel the notification channel attempted
     * @param errorMessage the error description
     * @return a new failure NotificationLog
     */
    public static NotificationLog createFailureLog(Message message, User user, NotificationChannel channel, String errorMessage) {
        return NotificationLog.builder()
                .messageId(message.getId())
                .messageContent(message.getContent())
                .messageCategory(message.getCategory())
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userPhone(user.getPhoneNumber())
                .channel(channel)
                .status(NotificationStatus.FAILED)
                .sentAt(LocalDateTime.now())
                .errorMessage(errorMessage)
                .build();
    }
}