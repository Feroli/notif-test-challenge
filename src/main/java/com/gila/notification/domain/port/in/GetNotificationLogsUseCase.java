package com.gila.notification.domain.port.in;

import com.gila.notification.domain.model.NotificationLog;

import java.util.List;

public interface GetNotificationLogsUseCase {
    List<NotificationLog> getAllLogs();
    List<NotificationLog> getLogsByUserId(Long userId);
    List<NotificationLog> getLogsByMessageId(Long messageId);
}