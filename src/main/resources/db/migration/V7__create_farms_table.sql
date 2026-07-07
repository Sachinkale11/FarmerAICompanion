CREATE TABLE farms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    area_value NUMERIC(10, 4) NOT NULL,
    area_unit VARCHAR(50) NOT NULL,
    soil_type VARCHAR(50) NOT NULL,
    irrigation_type VARCHAR(100),
    gps_lat NUMERIC(9, 6),
    gps_lng NUMERIC(9, 6),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_farms_farmer
        FOREIGN KEY (farmer_id) REFERENCES farmers(id)
            ON DELETE RESTRICT,
    CONSTRAINT uk_farms_farmer_name UNIQUE (farmer_id, name)
);

CREATE INDEX idx_farms_farmer_id ON farms(farmer_id);
