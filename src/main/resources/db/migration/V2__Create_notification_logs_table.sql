-- Create notification_logs table
CREATE TABLE notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    message_content TEXT NOT NULL,
    message_category VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100),
    user_phone VARCHAR(20),
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(10) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,
    CONSTRAINT chk_log_category CHECK (message_category IN ('SPORTS', 'FINANCE', 'MOVIES')),
    CONSTRAINT chk_channel CHECK (channel IN ('SMS', 'EMAIL', 'PUSH_NOTIFICATION')),
    CONSTRAINT chk_status CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING'))
);

-- Add indexes for optimal query performance
CREATE INDEX idx_notification_user_id ON notification_logs(user_id);
CREATE INDEX idx_notification_message_id ON notification_logs(message_id);
CREATE INDEX idx_notification_sent_at ON notification_logs(sent_at DESC);
CREATE INDEX idx_notification_status ON notification_logs(status);
CREATE INDEX idx_notification_channel ON notification_logs(channel);
CREATE INDEX idx_notification_user_status ON notification_logs(user_id, status);
CREATE INDEX idx_notification_category_date ON notification_logs(message_category, sent_at DESC);