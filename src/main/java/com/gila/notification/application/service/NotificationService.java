package com.gila.notification.application.service;

import com.gila.notification.domain.model.*;
import com.gila.notification.domain.port.in.GetNotificationLogsUseCase;
import com.gila.notification.domain.port.in.SendMessageUseCase;
import com.gila.notification.domain.port.out.NotificationSender;
import com.gila.notification.domain.port.out.UserRepository;
import com.gila.notification.domain.service.NotificationStrategy;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.MessageEntity;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.NotificationLogEntity;
import com.gila.notification.infrastructure.adapter.out.persistence.repository.MessageRepository;
import com.gila.notification.infrastructure.adapter.out.persistence.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core service for handling notification sending and log management.
 * Implements business logic for message distribution and tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService implements SendMessageUseCase, GetNotificationLogsUseCase {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationStrategy notificationStrategy;

    @Override
    public SendMessageResult sendMessage(SendMessageCommand command) {
        log.info("Processing message for category: {}", command.category());

        if (command.content() == null || command.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setCategory(command.category());
        messageEntity.setContent(command.content());
        messageEntity = messageRepository.save(messageEntity);

        Message message = mapToMessage(messageEntity);

        List<User> subscribedUsers = userRepository.findBySubscribedCategory(command.category());
        log.info("Found {} users subscribed to category {}", subscribedUsers.size(), command.category());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (User user : subscribedUsers) {
            for (NotificationChannel channel : user.getChannels()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    sendNotificationToUser(message, user, channel, successCount, failureCount);
                });
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Message processing completed. Success: {}, Failures: {}",
                successCount.get(), failureCount.get());

        return new SendMessageResult(
                message.getId(),
                subscribedUsers.size(),
                successCount.get(),
                failureCount.get()
        );
    }

    private void sendNotificationToUser(Message message, User user, NotificationChannel channel,
                                         AtomicInteger successCount, AtomicInteger failureCount) {
        try {
            NotificationSender sender = notificationStrategy.getSender(channel);
            sender.send(message, user);

            NotificationLog successLog = NotificationLog.createSuccessLog(message, user, channel);
            saveNotificationLog(successLog);

            successCount.incrementAndGet();
            log.debug("Successfully sent {} notification to user {}", channel, user.getName());

        } catch (Exception e) {
            NotificationLog failureLog = NotificationLog.createFailureLog(message, user, channel, e.getMessage());
            saveNotificationLog(failureLog);

            failureCount.incrementAndGet();
            log.error("Failed to send {} notification to user {}: {}",
                    channel, user.getName(), e.getMessage());
        }
    }

    private void saveNotificationLog(NotificationLog log) {
        NotificationLogEntity entity = mapToEntity(log);
        notificationLogRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationLog> getAllLogs() {
        List<NotificationLogEntity> entities = notificationLogRepository.findAllByOrderBySentAtDesc();
        return entities.stream().map(this::mapToNotificationLog).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationLog> getLogsByUserId(Long userId) {
        List<NotificationLogEntity> entities = notificationLogRepository.findByUserIdOrderBySentAtDesc(userId);
        return entities.stream().map(this::mapToNotificationLog).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationLog> getLogsByMessageId(Long messageId) {
        List<NotificationLogEntity> entities = notificationLogRepository.findByMessageIdOrderBySentAtDesc(messageId);
        return entities.stream().map(this::mapToNotificationLog).toList();
    }

    private Message mapToMessage(MessageEntity entity) {
        return Message.builder()
                .id(entity.getId())
                .category(entity.getCategory())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private NotificationLogEntity mapToEntity(NotificationLog log) {
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setMessageId(log.getMessageId());
        entity.setMessageContent(log.getMessageContent());
        entity.setMessageCategory(log.getMessageCategory());
        entity.setUserId(log.getUserId());
        entity.setUserName(log.getUserName());
        entity.setUserEmail(log.getUserEmail());
        entity.setUserPhone(log.getUserPhone());
        entity.setChannel(log.getChannel());
        entity.setStatus(log.getStatus());
        entity.setSentAt(log.getSentAt());
        entity.setErrorMessage(log.getErrorMessage());
        return entity;
    }

    private NotificationLog mapToNotificationLog(NotificationLogEntity entity) {
        return NotificationLog.builder()
                .id(entity.getId())
                .messageId(entity.getMessageId())
                .messageContent(entity.getMessageContent())
                .messageCategory(entity.getMessageCategory())
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .userEmail(entity.getUserEmail())
                .userPhone(entity.getUserPhone())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .sentAt(entity.getSentAt())
                .errorMessage(entity.getErrorMessage())
                .build();
    }
}