CREATE TABLE crops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    variety VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    season VARCHAR(50) NOT NULL,
    sowing_date DATE,
    expected_harvest_date DATE,
    actual_harvest_date DATE,
    expected_yield_value NUMERIC(10, 2),
    yield_unit VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_crops_farm
        FOREIGN KEY (farm_id) REFERENCES farms(id)
            ON DELETE RESTRICT
);

CREATE INDEX idx_crops_farm_id ON crops(farm_id);
CREATE INDEX idx_crops_status ON crops(status);
