package com.gila.notification.infrastructure.adapter.out.notification;

import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Push notification sender implementation.
 * Simulates sending push notifications through services like FCM or APNS.
 */
@Component
@Slf4j
public class PushNotificationSender implements NotificationSender {

    private static final double FAILURE_RATE = 0.15;
    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final String DEVICE_TOKEN_PREFIX = "device_";
    private static final String PAYLOAD_TEMPLATE = "{\"to\": \"%s\", \"notification\": {\"title\": \"%s\", \"body\": \"%s\"}}";
    private static final String ERROR_SERVICE_UNAVAILABLE = "Push notification service temporarily unavailable";
    private static final String ERROR_INVALID_TOKEN = "Invalid device token";
    private static final String ERROR_MESSAGE_TOO_LONG = "Message too long for push notification";

    /**
     * Sends a push notification to a user's device.
     *
     * @param message the message to send
     * @param user the recipient user
     * @throws NotificationException if sending fails
     */
    @Override
    public void send(Message message, User user) throws NotificationException {
        String deviceToken = DEVICE_TOKEN_PREFIX + user.getId();

        log.info("Sending Push Notification to {} (Device: {}): Category: {}, Message: {}",
                user.getName(), deviceToken, message.getCategory(), message.getContent());

        simulatePushService(deviceToken, message.getCategory().getDisplayName(), message.getContent());
    }

    /**
     * Returns the notification channel type.
     *
     * @return PUSH_NOTIFICATION channel
     */
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH_NOTIFICATION;
    }

    private void simulatePushService(String deviceToken, String title, String body) throws NotificationException {
        if (Math.random() < FAILURE_RATE) {
            throw new NotificationException(ERROR_SERVICE_UNAVAILABLE);
        }

        if (deviceToken == null || deviceToken.isEmpty()) {
            throw new NotificationException(ERROR_INVALID_TOKEN);
        }

        String payload = String.format(PAYLOAD_TEMPLATE, deviceToken, title, body);
        log.debug("Push notification payload: {}", payload);

        if (body.length() > MAX_MESSAGE_LENGTH) {
            throw new NotificationException(ERROR_MESSAGE_TOO_LONG);
        }
    }
}