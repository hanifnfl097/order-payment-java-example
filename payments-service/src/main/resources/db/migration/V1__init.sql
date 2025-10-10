
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
INSERT INTO payments(order_id, amount, status) VALUES
(1, 119.98, 'PENDING'),
(2, 59.99, 'FAILED'),
(3, 39.99, 'PAID');
