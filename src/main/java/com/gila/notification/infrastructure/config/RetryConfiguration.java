package com.gila.notification.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

/**
 * Configuration for retry mechanism to handle transient failures.
 * Enables fault tolerance for notification sending operations.
 */
@Configuration
@EnableRetry
public class RetryConfiguration { }