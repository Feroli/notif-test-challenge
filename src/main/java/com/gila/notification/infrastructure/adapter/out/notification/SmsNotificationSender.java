package com.gila.notification.infrastructure.adapter.out.notification;

import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SMS notification sender implementation.
 * Simulates sending SMS messages through a gateway service.
 */
@Component
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    private static final double FAILURE_RATE = 0.1;
    private static final String PHONE_REGEX = "\\+?[1-9]\\d{1,14}";
    private static final int SMS_CHARACTER_LIMIT = 160;
    private static final String ERROR_NO_PHONE = "User does not have a phone number";
    private static final String ERROR_GATEWAY_UNAVAILABLE = "SMS gateway temporarily unavailable";
    private static final String ERROR_INVALID_PHONE = "Invalid phone number format: ";
    private static final String WARN_MESSAGE_TOO_LONG = "Message exceeds SMS character limit, will be sent as multiple parts";

    /**
     * Sends an SMS notification to a user.
     *
     * @param message the message to send
     * @param user the recipient user
     * @throws NotificationException if sending fails
     */
    @Override
    public void send(Message message, User user) throws NotificationException {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new NotificationException(ERROR_NO_PHONE);
        }

        log.info("Sending SMS to {} ({}): Category: {}, Message: {}",
                user.getName(), user.getPhoneNumber(), message.getCategory(), message.getContent());

        simulateSmsGateway(user.getPhoneNumber(), message.getContent());
    }

    /**
     * Returns the notification channel type.
     *
     * @return SMS notification channel
     */
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    private void simulateSmsGateway(String phoneNumber, String message) throws NotificationException {
        if (Math.random() < FAILURE_RATE) {
            throw new NotificationException(ERROR_GATEWAY_UNAVAILABLE);
        }

        if (!phoneNumber.matches(PHONE_REGEX)) {
            throw new NotificationException(ERROR_INVALID_PHONE + phoneNumber);
        }

        if (message.length() > SMS_CHARACTER_LIMIT) {
            log.warn(WARN_MESSAGE_TOO_LONG);
        }
    }
}