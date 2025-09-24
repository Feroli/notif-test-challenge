package com.gila.notification.application.mapper;

import com.gila.notification.application.dto.NotificationLogDto;
import com.gila.notification.domain.model.NotificationLog;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationLogDto toDto(NotificationLog log) {
        return NotificationLogDto.builder()
                .id(log.getId())
                .messageId(log.getMessageId())
                .messageContent(log.getMessageContent())
                .messageCategory(log.getMessageCategory())
                .userId(log.getUserId())
                .userName(log.getUserName())
                .userEmail(log.getUserEmail())
                .userPhone(log.getUserPhone())
                .channel(log.getChannel())
                .status(log.getStatus())
                .sentAt(log.getSentAt())
                .errorMessage(log.getErrorMessage())
                .build();
    }
}