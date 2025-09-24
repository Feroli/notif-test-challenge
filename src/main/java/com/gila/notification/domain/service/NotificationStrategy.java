package com.gila.notification.domain.service;

import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.port.out.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Strategy pattern implementation for selecting notification senders.
 * Manages the mapping between notification channels and their senders.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationStrategy {

    private final List<NotificationSender> notificationSenders;

    private Map<NotificationChannel, NotificationSender> senderMap;

    /**
     * Initializes the sender map after dependency injection.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        senderMap = notificationSenders.stream()
                .collect(Collectors.toMap(
                        NotificationSender::getChannel,
                        Function.identity()
                ));
        log.info("Initialized notification strategy with {} senders", senderMap.size());
    }

    /**
     * Retrieves the appropriate sender for a notification channel.
     *
     * @param channel the notification channel
     * @return the corresponding NotificationSender
     * @throws IllegalArgumentException if no sender exists for the channel
     */
    public NotificationSender getSender(NotificationChannel channel) {
        return Optional.ofNullable(senderMap.get(channel))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No notification sender found for channel: " + channel
                ));
    }

    /**
     * Checks if a notification channel is supported.
     *
     * @param channel the channel to check
     * @return true if supported, false otherwise
     */
    public boolean isChannelSupported(NotificationChannel channel) {
        return senderMap.containsKey(channel);
    }

    /**
     * Gets all supported notification channels.
     *
     * @return list of supported channels
     */
    public List<NotificationChannel> getSupportedChannels() {
        return List.copyOf(senderMap.keySet());
    }
}