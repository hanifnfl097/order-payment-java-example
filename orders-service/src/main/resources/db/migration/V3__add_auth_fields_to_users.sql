-- V3__add_auth_fields_to_users.sql
-- Tambah kolom password, role, created_at, updated_at ke tabel users.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password VARCHAR(255),
    ADD COLUMN IF NOT EXISTS role VARCHAR(20),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- Set nilai default untuk user yang sudah ada (Alice, Bob, dll)
UPDATE users
SET
    role = COALESCE(role, 'CUSTOMER'),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW());

-- Wajibkan role + timestamps tidak null
ALTER TABLE users
    ALTER COLUMN role SET NOT NULL,
ALTER COLUMN created_at SET NOT NULL,
  ALTER COLUMN updated_at SET NOT NULL;
