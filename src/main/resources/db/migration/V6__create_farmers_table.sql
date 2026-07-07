CREATE TABLE farmers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    preferred_language VARCHAR(10) DEFAULT 'en',
    address JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_farmers_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT
);

CREATE INDEX idx_farmers_user_id ON farmers(user_id);
