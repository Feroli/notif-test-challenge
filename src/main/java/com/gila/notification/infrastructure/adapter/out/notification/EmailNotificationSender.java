package com.gila.notification.infrastructure.adapter.out.notification;

import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private static final double FAILURE_RATE = 0.05;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String EMAIL_TEMPLATE = "To: %s%nSubject: Notification - %s%nBody: %s";
    private static final String ERROR_NO_EMAIL = "User does not have an email address";
    private static final String ERROR_SERVICE_UNAVAILABLE = "Email service temporarily unavailable";
    private static final String ERROR_INVALID_FORMAT = "Invalid email format: ";

    @Override
    @Retryable(
            value = {NotificationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void send(Message message, User user) throws NotificationException {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new NotificationException(ERROR_NO_EMAIL);
        }

        log.info("Sending Email to {} ({}): Category: {}, Message: {}",
                user.getName(), user.getEmail(), message.getCategory(), message.getContent());

        simulateEmailService(user.getEmail(), message.getCategory().getDisplayName(), message.getContent());
    }

    /**
     * Recovery method called when all retry attempts fail.
     */
    @Recover
    public void recover(NotificationException e, Message message, User user) throws NotificationException {
        log.error("Failed to send email to {} after retries: {}", user.getEmail(), e.getMessage());
        throw new NotificationException("Email delivery failed after 3 attempts: " + e.getMessage());
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    private void simulateEmailService(String email, String subject, String body) throws NotificationException {
        if (Math.random() < FAILURE_RATE) {
            throw new NotificationException(ERROR_SERVICE_UNAVAILABLE);
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new NotificationException(ERROR_INVALID_FORMAT + email);
        }

        String emailContent = String.format(EMAIL_TEMPLATE, email, subject, body);

        log.debug("Email composed: {}", emailContent);
    }
}