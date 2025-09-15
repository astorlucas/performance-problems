-- Sample data for performance testing

-- Users table (using INSERT IGNORE to avoid duplicate key errors)
INSERT INTO users (id, username, email, first_name, last_name, created_at, updated_at) VALUES
(1, 'john_doe', 'john.doe@example.com', 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'jane_smith', 'jane.smith@example.com', 'Jane', 'Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'bob_wilson', 'bob.wilson@example.com', 'Bob', 'Wilson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'alice_brown', 'alice.brown@example.com', 'Alice', 'Brown', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'charlie_davis', 'charlie.davis@example.com', 'Charlie', 'Davis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'diana_miller', 'diana.miller@example.com', 'Diana', 'Miller', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'eve_jones', 'eve.jones@example.com', 'Eve', 'Jones', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'frank_garcia', 'frank.garcia@example.com', 'Frank', 'Garcia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'grace_lee', 'grace.lee@example.com', 'Grace', 'Lee', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'henry_taylor', 'henry.taylor@example.com', 'Henry', 'Taylor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- Products table
INSERT INTO products (id, name, description, price, category, stock_quantity, created_at, updated_at) VALUES
(1, 'Laptop Pro', 'High-performance laptop for professionals', 1299.99, 'Electronics', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 'Electronics', 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Mechanical Keyboard', 'RGB mechanical keyboard', 149.99, 'Electronics', 75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Office Chair', 'Comfortable office chair', 299.99, 'Furniture', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Standing Desk', 'Adjustable standing desk', 599.99, 'Furniture', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Monitor 27"', '4K 27-inch monitor', 399.99, 'Electronics', 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Webcam HD', '1080p webcam for video calls', 79.99, 'Electronics', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Desk Lamp', 'LED desk lamp with USB ports', 49.99, 'Furniture', 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Noise Cancelling Headphones', 'Premium noise cancelling headphones', 249.99, 'Electronics', 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'USB-C Hub', 'Multi-port USB-C hub', 89.99, 'Electronics', 80, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Orders table
INSERT INTO orders (id, user_id, total_amount, status, order_date, created_at, updated_at) VALUES
(1, 1, 1329.98, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 179.98, 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 449.98, 'SHIPPED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 899.98, 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 329.98, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, 129.98, 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 249.99, 'SHIPPED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, 89.99, 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 9, 599.99, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, 199.98, 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Order Items table
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, created_at, updated_at) VALUES
(1, 1, 1, 1, 1299.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 2, 1, 29.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 2, 3, 1, 149.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, 4, 1, 29.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 3, 5, 1, 399.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 3, 6, 1, 49.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 4, 7, 1, 599.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 4, 8, 1, 299.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 5, 9, 1, 249.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 5, 10, 1, 79.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 6, 1, 1, 89.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 6, 2, 1, 39.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 7, 3, 1, 249.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 8, 4, 1, 89.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 9, 5, 1, 599.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 10, 6, 1, 199.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
