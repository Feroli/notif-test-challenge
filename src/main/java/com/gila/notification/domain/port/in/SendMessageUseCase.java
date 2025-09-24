package com.gila.notification.domain.port.in;

import com.gila.notification.domain.model.Category;

public interface SendMessageUseCase {
    SendMessageResult sendMessage(SendMessageCommand command);

    record SendMessageCommand(
            Category category,
            String content
    ) {}

    record SendMessageResult(
            Long messageId,
            int totalUsers,
            int successfulNotifications,
            int failedNotifications
    ) {}
}