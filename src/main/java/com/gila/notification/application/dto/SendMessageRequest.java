package com.gila.notification.application.dto;

import com.gila.notification.domain.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for sending notification messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Message content cannot be empty")
    private String message;
}