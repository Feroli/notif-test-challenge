package com.gila.notification.domain.port.out;

import com.gila.notification.domain.model.Message;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;

public interface NotificationSender {
    void send(Message message, User user) throws NotificationException;
    NotificationChannel getChannel();

    class NotificationException extends Exception {
        public NotificationException(String message) {
            super(message);
        }

        public NotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}