package com.gila.notification.application.service;

import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.NotificationLog;
import com.gila.notification.domain.model.NotificationStatus;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.in.SendMessageUseCase;
import com.gila.notification.domain.port.out.NotificationSender;
import com.gila.notification.domain.port.out.UserRepository;
import com.gila.notification.domain.service.NotificationStrategy;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.MessageEntity;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.NotificationLogEntity;
import com.gila.notification.infrastructure.adapter.out.persistence.repository.MessageRepository;
import com.gila.notification.infrastructure.adapter.out.persistence.repository.NotificationLogRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private NotificationStrategy notificationStrategy;

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationSender smsSender;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(
                userRepository,
                messageRepository,
                notificationLogRepository,
                notificationStrategy
        );
    }

    @Test
    @DisplayName("Should send message successfully to all subscribed users")
    void sendMessage_WhenUsersSubscribed_SendsToAllChannels() throws Exception {
        SendMessageUseCase.SendMessageCommand command = new SendMessageUseCase.SendMessageCommand(
                Category.SPORTS,
                "Sports news update"
        );

        MessageEntity savedMessage = new MessageEntity();
        savedMessage.setId(1L);
        savedMessage.setCategory(Category.SPORTS);
        savedMessage.setContent("Sports news update");

        User user1 = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("+1234567890")
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS))
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane@example.com")
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build();

        when(messageRepository.save(any(MessageEntity.class))).thenReturn(savedMessage);
        when(userRepository.findBySubscribedCategory(Category.SPORTS))
                .thenReturn(List.of(user1, user2));
        when(notificationStrategy.getSender(NotificationChannel.EMAIL)).thenReturn(emailSender);
        when(notificationStrategy.getSender(NotificationChannel.SMS)).thenReturn(smsSender);

        SendMessageUseCase.SendMessageResult result = service.sendMessage(command);

        assertNotNull(result);
        assertEquals(1L, result.messageId());
        assertEquals(2, result.totalUsers());
        assertEquals(3, result.successfulNotifications()); // 2 emails + 1 SMS
        assertEquals(0, result.failedNotifications());

        verify(emailSender, times(2)).send(any(Message.class), any(User.class));
        verify(smsSender, times(1)).send(any(Message.class), any(User.class));
        verify(notificationLogRepository, times(3)).save(any(NotificationLogEntity.class));
    }

    @Test
    @DisplayName("Should handle failed notifications gracefully")
    void sendMessage_WhenNotificationFails_LogsFailure() throws Exception {
        SendMessageUseCase.SendMessageCommand command = new SendMessageUseCase.SendMessageCommand(
                Category.FINANCE,
                "Market update"
        );

        MessageEntity savedMessage = new MessageEntity();
        savedMessage.setId(1L);
        savedMessage.setCategory(Category.FINANCE);
        savedMessage.setContent("Market update");

        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .subscribedCategories(Set.of(Category.FINANCE))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build();

        when(messageRepository.save(any(MessageEntity.class))).thenReturn(savedMessage);
        when(userRepository.findBySubscribedCategory(Category.FINANCE))
                .thenReturn(List.of(user));
        when(notificationStrategy.getSender(NotificationChannel.EMAIL)).thenReturn(emailSender);
        doThrow(new NotificationSender.NotificationException("Email service down"))
                .when(emailSender).send(any(Message.class), any(User.class));

        SendMessageUseCase.SendMessageResult result = service.sendMessage(command);

        assertEquals(0, result.successfulNotifications());
        assertEquals(1, result.failedNotifications());

        ArgumentCaptor<NotificationLogEntity> captor = ArgumentCaptor.forClass(NotificationLogEntity.class);
        verify(notificationLogRepository).save(captor.capture());

        NotificationLogEntity logEntity = captor.getValue();
        assertEquals(NotificationStatus.FAILED, logEntity.getStatus());
        assertEquals("Email service down", logEntity.getErrorMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty message content")
    void sendMessage_WhenMessageEmpty_ThrowsException() {
        SendMessageUseCase.SendMessageCommand command = new SendMessageUseCase.SendMessageCommand(
                Category.SPORTS,
                ""
        );

        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(command));
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty result when no users subscribed")
    void sendMessage_WhenNoUsersSubscribed_ReturnsEmptyResult() {
        SendMessageUseCase.SendMessageCommand command = new SendMessageUseCase.SendMessageCommand(
                Category.MOVIES,
                "New movie release"
        );

        MessageEntity savedMessage = new MessageEntity();
        savedMessage.setId(1L);
        savedMessage.setCategory(Category.MOVIES);
        savedMessage.setContent("New movie release");

        when(messageRepository.save(any(MessageEntity.class))).thenReturn(savedMessage);
        when(userRepository.findBySubscribedCategory(Category.MOVIES))
                .thenReturn(List.of());

        SendMessageUseCase.SendMessageResult result = service.sendMessage(command);

        assertEquals(0, result.totalUsers());
        assertEquals(0, result.successfulNotifications());
        assertEquals(0, result.failedNotifications());
        verify(notificationStrategy, never()).getSender(any());
    }

    @Test
    @DisplayName("Should retrieve all notification logs")
    void getAllLogs_ReturnsAllLogs() {
        NotificationLogEntity log1 = new NotificationLogEntity();
        log1.setId(1L);
        log1.setUserId(1L);
        log1.setUserName("John Doe");
        log1.setChannel(NotificationChannel.EMAIL);
        log1.setStatus(NotificationStatus.SUCCESS);

        NotificationLogEntity log2 = new NotificationLogEntity();
        log2.setId(2L);
        log2.setUserId(2L);
        log2.setUserName("Jane Smith");
        log2.setChannel(NotificationChannel.SMS);
        log2.setStatus(NotificationStatus.FAILED);

        when(notificationLogRepository.findAllByOrderBySentAtDesc())
                .thenReturn(List.of(log1, log2));

        List<NotificationLog> logs = service.getAllLogs();

        assertEquals(2, logs.size());
        assertEquals(1L, logs.get(0).getId());
        assertEquals(2L, logs.get(1).getId());
    }

    @Test
    @DisplayName("Should retrieve logs by user ID")
    void getLogsByUserId_ReturnsUserLogs() {
        Long userId = 1L;
        NotificationLogEntity log = new NotificationLogEntity();
        log.setId(1L);
        log.setUserId(userId);

        when(notificationLogRepository.findByUserIdOrderBySentAtDesc(userId))
                .thenReturn(List.of(log));

        List<NotificationLog> logs = service.getLogsByUserId(userId);

        assertEquals(1, logs.size());
        assertEquals(userId, logs.getFirst().getUserId());
    }

    @Test
    @DisplayName("Should retrieve logs by message ID")
    void getLogsByMessageId_ReturnsMessageLogs() {
        Long messageId = 1L;
        NotificationLogEntity log = new NotificationLogEntity();
        log.setId(1L);
        log.setMessageId(messageId);

        when(notificationLogRepository.findByMessageIdOrderBySentAtDesc(messageId))
                .thenReturn(List.of(log));

        List<NotificationLog> logs = service.getLogsByMessageId(messageId);

        assertEquals(1, logs.size());
        assertEquals(messageId, logs.getFirst().getMessageId());
    }
}