-- =====================================================
-- V4: Seed default roles
-- =====================================================
INSERT INTO roles (id, name, description) VALUES
    (gen_random_uuid(), 'ADMIN',      'System administrator with full access'),
    (gen_random_uuid(), 'FARMER',     'Farmer user with standard access'),
    (gen_random_uuid(), 'AGRONOMIST', 'Agronomist with advisory access');
