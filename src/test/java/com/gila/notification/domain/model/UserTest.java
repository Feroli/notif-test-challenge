package com.gila.notification.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should return true when user is subscribed to category")
    void isSubscribedTo_WhenUserIsSubscribed_ReturnsTrue() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(Set.of(Category.SPORTS, Category.FINANCE))
                .build();

        assertTrue(user.isSubscribedTo(Category.SPORTS));
        assertTrue(user.isSubscribedTo(Category.FINANCE));
    }

    @Test
    @DisplayName("Should return false when user is not subscribed to category")
    void isSubscribedTo_WhenUserIsNotSubscribed_ReturnsFalse() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(Set.of(Category.SPORTS))
                .build();

        assertFalse(user.isSubscribedTo(Category.MOVIES));
    }

    @Test
    @DisplayName("Should return false when user has no subscriptions")
    void isSubscribedTo_WhenNoSubscriptions_ReturnsFalse() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(null)
                .build();

        assertFalse(user.isSubscribedTo(Category.SPORTS));
    }

    @Test
    @DisplayName("Should return true when user has channel")
    void hasChannel_WhenUserHasChannel_ReturnsTrue() {
        User user = User.builder()
                .id(1L)
                .channels(Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS))
                .build();

        assertTrue(user.hasChannel(NotificationChannel.EMAIL));
        assertTrue(user.hasChannel(NotificationChannel.SMS));
    }

    @Test
    @DisplayName("Should return false when user does not have channel")
    void hasChannel_WhenUserDoesNotHaveChannel_ReturnsFalse() {
        User user = User.builder()
                .id(1L)
                .channels(Set.of(NotificationChannel.EMAIL))
                .build();

        assertFalse(user.hasChannel(NotificationChannel.PUSH_NOTIFICATION));
    }

    @Test
    @DisplayName("Should return true when user should receive notification")
    void shouldReceiveNotification_WhenSubscribedAndHasChannels_ReturnsTrue() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build();

        assertTrue(user.shouldReceiveNotification(Category.SPORTS));
    }

    @Test
    @DisplayName("Should return false when user not subscribed")
    void shouldReceiveNotification_WhenNotSubscribed_ReturnsFalse() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build();

        assertFalse(user.shouldReceiveNotification(Category.FINANCE));
    }

    @Test
    @DisplayName("Should return false when user has no channels")
    void shouldReceiveNotification_WhenNoChannels_ReturnsFalse() {
        User user = User.builder()
                .id(1L)
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(new HashSet<>())
                .build();

        assertFalse(user.shouldReceiveNotification(Category.SPORTS));
    }
}