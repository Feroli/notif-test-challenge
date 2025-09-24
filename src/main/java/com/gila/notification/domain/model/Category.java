package com.gila.notification.domain.model;

/**
 * Represents the available message categories in the system.
 */
public enum Category {
    SPORTS("Sports"),
    FINANCE("Finance"),
    MOVIES("Movies");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}