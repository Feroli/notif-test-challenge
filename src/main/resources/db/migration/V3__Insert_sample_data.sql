-- Insert sample messages for each category
INSERT INTO messages (category, content, created_at) VALUES
    ('SPORTS', 'Breaking: Local team wins championship!', DATEADD('HOUR', -24, CURRENT_TIMESTAMP)),
    ('SPORTS', 'Match postponed due to weather conditions', DATEADD('HOUR', -12, CURRENT_TIMESTAMP)),
    ('FINANCE', 'Stock market reaches all-time high', DATEADD('HOUR', -8, CURRENT_TIMESTAMP)),
    ('FINANCE', 'New cryptocurrency regulations announced', DATEADD('HOUR', -4, CURRENT_TIMESTAMP)),
    ('MOVIES', 'Oscar nominations revealed', DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
    ('MOVIES', 'New blockbuster breaks box office records', DATEADD('HOUR', -1, CURRENT_TIMESTAMP));

-- Insert sample notification logs (to demonstrate the system has been running)
INSERT INTO notification_logs (message_id, message_content, message_category, user_id, user_name, user_email, user_phone, channel, status, sent_at, error_message) VALUES
    (1, 'Breaking: Local team wins championship!', 'SPORTS', 1, 'John Doe', 'john.doe@example.com', '+1234567890', 'EMAIL', 'SUCCESS', DATEADD('HOUR', -24, CURRENT_TIMESTAMP), NULL),
    (1, 'Breaking: Local team wins championship!', 'SPORTS', 1, 'John Doe', 'john.doe@example.com', '+1234567890', 'SMS', 'SUCCESS', DATEADD('HOUR', -24, CURRENT_TIMESTAMP), NULL),
    (1, 'Breaking: Local team wins championship!', 'SPORTS', 3, 'Bob Johnson', 'bob.johnson@example.com', '+1234567892', 'SMS', 'FAILED', DATEADD('HOUR', -24, CURRENT_TIMESTAMP), 'SMS gateway temporarily unavailable'),
    (3, 'Stock market reaches all-time high', 'FINANCE', 1, 'John Doe', 'john.doe@example.com', '+1234567890', 'EMAIL', 'SUCCESS', DATEADD('HOUR', -8, CURRENT_TIMESTAMP), NULL),
    (3, 'Stock market reaches all-time high', 'FINANCE', 4, 'Alice Brown', 'alice.brown@example.com', '+1234567893', 'PUSH_NOTIFICATION', 'SUCCESS', DATEADD('HOUR', -8, CURRENT_TIMESTAMP), NULL),
    (5, 'Oscar nominations revealed', 'MOVIES', 2, 'Jane Smith', 'jane.smith@example.com', '+1234567891', 'EMAIL', 'SUCCESS', DATEADD('HOUR', -2, CURRENT_TIMESTAMP), NULL);