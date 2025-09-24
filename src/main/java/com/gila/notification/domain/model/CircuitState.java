package com.gila.notification.domain.model;

/**
 * Represents the possible states of a circuit breaker.
 * Used to manage fault tolerance in notification services.
 */
public enum CircuitState {
    /**
     * Normal operation - requests are allowed through.
     */
    CLOSED,

    /**
     * Circuit is open - requests are blocked due to failures.
     */
    OPEN,

    /**
     * Testing state - limited requests allowed to test recovery.
     */
    HALF_OPEN
}