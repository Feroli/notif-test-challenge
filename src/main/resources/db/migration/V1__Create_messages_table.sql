-- Create messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_category CHECK (category IN ('SPORTS', 'FINANCE', 'MOVIES'))
);

-- Add indexes for better performance
CREATE INDEX idx_message_category ON messages(category);
CREATE INDEX idx_message_created_at ON messages(created_at DESC);