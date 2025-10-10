
CREATE TABLE IF NOT EXISTS users ( id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(120) NOT NULL UNIQUE );
CREATE TABLE IF NOT EXISTS products ( id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, price NUMERIC(12,2) NOT NULL );
CREATE TABLE IF NOT EXISTS orders ( id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW() );
CREATE TABLE IF NOT EXISTS order_items ( id BIGSERIAL PRIMARY KEY, order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE, product_id BIGINT NOT NULL REFERENCES products(id), quantity INT NOT NULL );
INSERT INTO users(name, email) VALUES ('Alice','alice@example.com'),('Bob','bob@example.com');
INSERT INTO products(name, price) VALUES ('Keyboard',59.99),('Mouse',29.99),('Monitor',199.99),('USB Cable',9.99);
INSERT INTO orders(user_id) VALUES (1),(1),(2);
INSERT INTO order_items(order_id, product_id, quantity) VALUES (1,1,2),(1,2,1),(2,4,3),(3,3,1);
