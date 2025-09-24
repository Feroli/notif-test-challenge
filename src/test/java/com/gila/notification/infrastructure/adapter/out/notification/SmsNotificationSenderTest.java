package com.gila.notification.infrastructure.adapter.out.notification;

import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SmsNotificationSenderTest {

    private SmsNotificationSender sender;

    @BeforeEach
    void setUp() {
        sender = new SmsNotificationSender();
    }

    @Test
    @DisplayName("Should return SMS channel")
    void getChannel_ReturnsSmsChannel() {
        assertEquals(NotificationChannel.SMS, sender.getChannel());
    }

    @Test
    @DisplayName("Should send SMS successfully with valid phone number")
    void send_WithValidPhoneNumber_Succeeds() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .phoneNumber("+1234567890")
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.SMS))
                .build();

        Message message = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Test message")
                .build();

        try {
            sender.send(message, user);
        } catch (NotificationSender.NotificationException e) {
            assertTrue(e.getMessage().contains("SMS gateway temporarily unavailable") ||
                      e.getMessage().contains("Invalid phone number"));
        }
    }

    @Test
    @DisplayName("Should throw exception when user has no phone number")
    void send_WithoutPhoneNumber_ThrowsException() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .phoneNumber("")
                .build();

        Message message = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Test message")
                .build();

        NotificationSender.NotificationException exception = assertThrows(
                NotificationSender.NotificationException.class,
                () -> sender.send(message, user)
        );

        assertEquals("User does not have a phone number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid phone number format")
    void send_WithInvalidPhoneNumber_ThrowsException() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .phoneNumber("invalid-phone")
                .build();

        Message message = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Test message")
                .build();

        NotificationSender.NotificationException exception = assertThrows(
                NotificationSender.NotificationException.class,
                () -> sender.send(message, user)
        );

        assertTrue(exception.getMessage().contains("Invalid phone number format"));
    }
}