package com.gila.notification.domain.service;

import com.gila.notification.domain.model.CircuitState;
import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.NotificationSender;
import com.gila.notification.infrastructure.config.CircuitBreakerConfiguration.NotificationCircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Resilient notification service with circuit breaker and retry mechanisms.
 * Provides fault-tolerant notification delivery.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResilientNotificationService {

    private final NotificationStrategy notificationStrategy;
    private final NotificationCircuitBreaker circuitBreaker;

    /**
     * Sends a notification with circuit breaker protection.
     *
     * @param message the message to send
     * @param user the recipient
     * @param channel the notification channel
     * @throws NotificationSender.NotificationException if sending fails
     */
    public void sendWithCircuitBreaker(Message message, User user, NotificationChannel channel)
            throws NotificationSender.NotificationException {

        // Check circuit breaker state
        if (!circuitBreaker.allowRequest()) {
            log.warn("Circuit breaker is OPEN for channel {}. Skipping notification to user {}",
                    channel, user.getName());
            throw new NotificationSender.NotificationException(
                    "Service temporarily unavailable due to high failure rate");
        }

        try {
            // Attempt to send notification
            NotificationSender sender = notificationStrategy.getSender(channel);
            sender.send(message, user);

            // Record success
            circuitBreaker.recordSuccess();
            log.debug("Successfully sent {} notification through circuit breaker", channel);

        } catch (Exception e) {
            // Record failure
            circuitBreaker.recordFailure();
            log.error("Failed to send {} notification through circuit breaker: {}",
                    channel, e.getMessage());
            throw e;
        }
    }

    /**
     * Gets the current circuit breaker state.
     *
     * @return the circuit state
     */
    public CircuitState getCircuitState() {
        return circuitBreaker.getState();
    }

    /**
     * Resets the circuit breaker.
     */
    public void resetCircuitBreaker() {
        circuitBreaker.reset();
        log.info("Circuit breaker has been reset");
    }
}