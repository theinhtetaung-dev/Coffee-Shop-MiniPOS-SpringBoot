
DROP DATABASE IF EXISTS CoffeePOSDB;

CREATE DATABASE CoffeePOSDB;

USE CoffeePOSDB;


DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;


CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(18, 2) NOT NULL,
    category_id INT,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_Product_Category 
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON DELETE SET NULL
);


CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_code VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100),
    total_amount DECIMAL(18, 2) NOT NULL,
    discount_amount DECIMAL(18, 2) DEFAULT 0.00,
    net_amount DECIMAL(18, 2) NOT NULL,
    payment_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    unit_price DECIMAL(18, 2) NOT NULL,
    quantity INT NOT NULL,
    sub_amount DECIMAL(18, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_SaleItem_Sale 
        FOREIGN KEY (sale_id)
        REFERENCES sales(sale_id)
        ON DELETE CASCADE,
    CONSTRAINT FK_SaleItem_Product 
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE RESTRICT
);


-- Categories

INSERT INTO categories (category_code, category_name, description) VALUES
('C-1', 'Coffee', 'Coffee drinks'),
('C-2', 'Tea', 'Tea drinks'),
('C-3', 'Bakery', 'Bakery items'),
('C-4', 'Cold', 'Cold drinks'),
('C-5', 'Dessert', 'Desserts'),
('C-6', 'Snacks', 'Snacks'),
('C-7', 'Breakfast', 'Breakfast');


-- Products (40)

INSERT INTO products 
(product_code, product_name, description, price, category_id, is_available) VALUES

-- Coffee
('P-001', 'Espresso', '', 2500, 1, 1),
('P-002', 'Americano', '', 2800, 1, 1),
('P-003', 'Latte', '', 3500, 1, 1),
('P-004', 'Cappuccino', '', 3700, 1, 1),
('P-005', 'Mocha', '', 4000, 1, 1),
('P-006', 'Macchiato', '', 4500, 1, 1),
('P-007', 'Flat White', '', 3800, 1, 1),
('P-008', 'Vietnam Coffee', '', 4200, 1, 1),

-- Tea
('P-009', 'Green Tea', '', 2000, 2, 1),
('P-010', 'Milk Tea', '', 2800, 2, 1),
('P-011', 'Lemon Tea', '', 2500, 2, 1),
('P-012', 'Thai Tea', '', 3200, 2, 1),
('P-013', 'Matcha', '', 4200, 2, 1),
('P-014', 'Honey Tea', '', 3000, 2, 1),

-- Bakery
('P-015', 'Croissant', '', 1500, 3, 1),
('P-016', 'Choco Croissant', '', 2200, 3, 1),
('P-017', 'Muffin', '', 2500, 3, 1),
('P-018', 'Banana Bread', '', 2300, 3, 1),
('P-019', 'Danish', '', 2600, 3, 1),
('P-020', 'Toast', '', 1800, 3, 1),

-- Cold Drinks
('P-021', 'Iced Americano', '', 3000, 4, 1),
('P-022', 'Iced Latte', '', 3800, 4, 1),
('P-023', 'Iced Mocha', '', 4300, 4, 1),
('P-024', 'Lemon Soda', '', 2500, 4, 1),
('P-025', 'Strawberry Soda', '', 2800, 4, 1),
('P-026', 'Orange Juice', '', 3000, 4, 1),

-- Dessert
('P-027', 'Chocolate Cake', '', 3500, 5, 1),
('P-028', 'Cheesecake', '', 3800, 5, 1),
('P-029', 'Tiramisu', '', 4500, 5, 1),
('P-030', 'Ice Cream', '', 2500, 5, 1),
('P-031', 'Pudding', '', 2200, 5, 1),

-- Snacks
('P-032', 'Fries', '', 3000, 6, 1),
('P-033', 'Nuggets', '', 4500, 6, 1),
('P-034', 'Sandwich', '', 4000, 6, 1),
('P-035', 'Tuna Sandwich', '', 4500, 6, 1),
('P-036', 'Garlic Bread', '', 2800, 6, 1),

-- Breakfast
('P-037', 'Breakfast A', '', 6500, 7, 1),
('P-038', 'Breakfast B', '', 7000, 7, 1),
('P-039', 'Omelette', '', 5500, 7, 1),
('P-040', 'Pancake', '', 7500, 7, 1);


-- Sales (100)

INSERT INTO sales 
(sale_code, customer_name, total_amount, discount_amount, net_amount, payment_type, created_at)
SELECT 
    CONCAT('S-', 1000 + seq),
    CONCAT('Customer ', seq),
    0,
    0,
    0,
    CASE 
        WHEN seq % 3 = 0 THEN 'Cash'
        WHEN seq % 3 = 1 THEN 'KPay'
        ELSE 'WavePay'
    END,
    DATE_SUB(CURRENT_TIMESTAMP, INTERVAL FLOOR(seq / 5) DAY)
FROM (
    SELECT 1 seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
    UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25
    UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
    UNION ALL SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35
    UNION ALL SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40
    UNION ALL SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45
    UNION ALL SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50
    UNION ALL SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55
    UNION ALL SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60
    UNION ALL SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65
    UNION ALL SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70
    UNION ALL SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75
    UNION ALL SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79 UNION ALL SELECT 80
    UNION ALL SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85
    UNION ALL SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89 UNION ALL SELECT 90
    UNION ALL SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95
    UNION ALL SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99 UNION ALL SELECT 100
) t;


-- Sale Items

INSERT INTO sale_items 
(sale_id, product_id, unit_price, quantity, sub_amount)
SELECT 
    s.sale_id,
    (s.sale_id % 40) + 1,
    p.price,
    (s.sale_id % 3) + 1,
    p.price * ((s.sale_id % 3) + 1)
FROM sales s
JOIN products p 
    ON p.product_id = ((s.sale_id % 40) + 1);


-- Update totals

UPDATE sales s
JOIN (
    SELECT sale_id, SUM(sub_amount) total
    FROM sale_items
    GROUP BY sale_id
) t 
    ON s.sale_id = t.sale_id
SET 
    s.total_amount = t.total,
    s.net_amount = t.total - s.discount_amount;

