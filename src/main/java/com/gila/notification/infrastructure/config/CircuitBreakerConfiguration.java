package com.gila.notification.infrastructure.config;

import com.gila.notification.domain.model.CircuitState;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Configuration for circuit breaker pattern implementation.
 * Monitors and prevents cascading failures in notification services.
 */
@Configuration
public class CircuitBreakerConfiguration {

    /**
     * Simple circuit breaker implementation for notification services.
     */
    @Bean
    public NotificationCircuitBreaker notificationCircuitBreaker() {
        return new NotificationCircuitBreaker();
    }

    /**
     * Circuit breaker for handling notification service failures.
     */
    public static class NotificationCircuitBreaker {
        private static final int FAILURE_THRESHOLD = 5;
        private static final long TIMEOUT_DURATION_ONE_MINUTE = 60000;
        private static final long HALF_OPEN_SUCCESS_THRESHOLD = 3;

        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);

        @Getter
        private CircuitState state = CircuitState.CLOSED;

        /**
         * Checks if the circuit allows the request to proceed.
         */
        public boolean allowRequest() {
            return switch (state) {
                case CLOSED -> true;
                case OPEN -> {
                    if (System.currentTimeMillis() - lastFailureTime.get() > TIMEOUT_DURATION_ONE_MINUTE) {
                        state = CircuitState.HALF_OPEN;
                        successCount.set(0);
                        yield true;
                    }
                    yield false;
                }
                case HALF_OPEN -> true;
                default -> false;
            };
        }

        /**
         * Records a successful operation.
         */
        public void recordSuccess() {
            failureCount.set(0);
            if (state == CircuitState.HALF_OPEN) {
                int successes = successCount.incrementAndGet();
                if (successes >= HALF_OPEN_SUCCESS_THRESHOLD) {
                    state = CircuitState.CLOSED;
                }
            }
        }

        /**
         * Records a failed operation.
         */
        public void recordFailure() {
            lastFailureTime.set(System.currentTimeMillis());
            int failures = failureCount.incrementAndGet();

            if (state == CircuitState.HALF_OPEN || failures >= FAILURE_THRESHOLD) {
                state = CircuitState.OPEN;
            }
        }

        /**
         * Resets the circuit breaker.
         */
        public void reset() {
            failureCount.set(0);
            successCount.set(0);
            lastFailureTime.set(0);
            state = CircuitState.CLOSED;
        }
    }
}