-- V2__extend_products_for_ecommerce.sql
-- Extend the existing products table to support ecommerce catalog fields.

-- 1) Add new columns (initially nullable where needed)
ALTER TABLE products ADD COLUMN IF NOT EXISTS slug VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS stock_quantity INTEGER;
ALTER TABLE products ADD COLUMN IF NOT EXISTS category VARCHAR(100);
ALTER TABLE products ADD COLUMN IF NOT EXISTS brand VARCHAR(100);
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url VARCHAR(1024);
ALTER TABLE products ADD COLUMN IF NOT EXISTS additional_images TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS specs TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- 2) Initialize data for existing rows
UPDATE products
SET
    slug = COALESCE(
            slug,
            regexp_replace(lower(name), '\s+', '-', 'g')  -- basic slug: "Gaming Mouse" -> "gaming-mouse"
           ),
    stock_quantity = COALESCE(stock_quantity, 0),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW());

-- 3) Enforce NOT NULL where appropriate
ALTER TABLE products
    ALTER COLUMN stock_quantity SET NOT NULL,
ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL,
    ALTER COLUMN slug SET NOT NULL;

-- 4) Unique constraint on slug
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM   pg_constraint
        WHERE  conname = 'uq_products_slug'
    ) THEN
ALTER TABLE products
    ADD CONSTRAINT uq_products_slug UNIQUE (slug);
END IF;
END$$;

-- 5) Helpful indexes for filtering / sorting
CREATE INDEX IF NOT EXISTS idx_products_category     ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_brand        ON products(brand);
CREATE INDEX IF NOT EXISTS idx_products_price        ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_created_at   ON products(created_at);
