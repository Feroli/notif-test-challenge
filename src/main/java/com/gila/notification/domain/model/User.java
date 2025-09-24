package com.gila.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Domain model representing a user in the notification system.
 * Contains user details and notification preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Set<Category> subscribedCategories;
    private Set<NotificationChannel> channels;

    /**
     * Checks if the user is subscribed to a specific category.
     *
     * @param category the category to check
     * @return true if subscribed, false otherwise
     */
    public boolean isSubscribedTo(Category category) {
        return subscribedCategories != null && subscribedCategories.contains(category);
    }

    /**
     * Checks if the user has enabled a specific notification channel.
     *
     * @param channel the channel to check
     * @return true if enabled, false otherwise
     */
    public boolean hasChannel(NotificationChannel channel) {
        return channels != null && channels.contains(channel);
    }

    /**
     * Determines if the user should receive notifications for a category.
     *
     * @param category the category to check
     * @return true if user is subscribed and has channels enabled
     */
    public boolean shouldReceiveNotification(Category category) {
        return isSubscribedTo(category) && channels != null && !channels.isEmpty();
    }
}