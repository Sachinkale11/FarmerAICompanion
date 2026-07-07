-- V5: Add device info to refresh tokens

ALTER TABLE refresh_tokens
ADD COLUMN device_id VARCHAR(255) NOT NULL DEFAULT 'unknown',
ADD COLUMN device_name VARCHAR(255),
ADD COLUMN platform VARCHAR(50),
ADD COLUMN last_used_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW();
