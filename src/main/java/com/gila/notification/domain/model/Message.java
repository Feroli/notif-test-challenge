package com.gila.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing a notification message.
 * Contains the message content and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private Category category;
    private String content;
    private LocalDateTime createdAt;

    /**
     * Factory method to create a new message.
     *
     * @param category the message category
     * @param content the message content
     * @return a new Message instance with current timestamp
     */
    public static Message create(Category category, String content) {
        return Message.builder()
                .category(category)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}