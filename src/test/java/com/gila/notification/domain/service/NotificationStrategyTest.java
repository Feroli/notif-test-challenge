package com.gila.notification.domain.service;

import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.port.out.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationStrategyTest {

    @Mock
    private NotificationSender smsSender;

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationSender pushSender;

    private NotificationStrategy strategy;

    @BeforeEach
    void setUp() {
        when(smsSender.getChannel()).thenReturn(NotificationChannel.SMS);
        when(emailSender.getChannel()).thenReturn(NotificationChannel.EMAIL);
        when(pushSender.getChannel()).thenReturn(NotificationChannel.PUSH_NOTIFICATION);

        strategy = new NotificationStrategy(List.of(smsSender, emailSender, pushSender));
        strategy.init();
    }

    @Test
    @DisplayName("Should return correct sender for SMS channel")
    void getSender_ForSmsChannel_ReturnsSmsSender() {
        NotificationSender sender = strategy.getSender(NotificationChannel.SMS);
        assertEquals(smsSender, sender);
    }

    @Test
    @DisplayName("Should return correct sender for Email channel")
    void getSender_ForEmailChannel_ReturnsEmailSender() {
        NotificationSender sender = strategy.getSender(NotificationChannel.EMAIL);
        assertEquals(emailSender, sender);
    }

    @Test
    @DisplayName("Should return correct sender for Push channel")
    void getSender_ForPushChannel_ReturnsPushSender() {
        NotificationSender sender = strategy.getSender(NotificationChannel.PUSH_NOTIFICATION);
        assertEquals(pushSender, sender);
    }

    @Test
    @DisplayName("Should throw exception for unsupported channel")
    void getSender_ForUnsupportedChannel_ThrowsException() {
        strategy = new NotificationStrategy(List.of(smsSender, emailSender));
        strategy.init();

        assertThrows(IllegalArgumentException.class,
                () -> strategy.getSender(NotificationChannel.PUSH_NOTIFICATION));
    }

    @Test
    @DisplayName("Should correctly identify supported channels")
    void isChannelSupported_ReturnsCorrectResult() {
        assertTrue(strategy.isChannelSupported(NotificationChannel.SMS));
        assertTrue(strategy.isChannelSupported(NotificationChannel.EMAIL));
        assertTrue(strategy.isChannelSupported(NotificationChannel.PUSH_NOTIFICATION));
    }

    @Test
    @DisplayName("Should return list of all supported channels")
    void getSupportedChannels_ReturnsAllChannels() {
        List<NotificationChannel> supportedChannels = strategy.getSupportedChannels();

        assertEquals(3, supportedChannels.size());
        assertTrue(supportedChannels.contains(NotificationChannel.SMS));
        assertTrue(supportedChannels.contains(NotificationChannel.EMAIL));
        assertTrue(supportedChannels.contains(NotificationChannel.PUSH_NOTIFICATION));
    }
}