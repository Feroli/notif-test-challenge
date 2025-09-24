package com.gila.notification.domain.model;

/**
 * Represents the available notification delivery channels.
 */
public enum NotificationChannel {
    SMS("SMS"),
    EMAIL("E-Mail"),
    PUSH_NOTIFICATION("Push Notification");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}