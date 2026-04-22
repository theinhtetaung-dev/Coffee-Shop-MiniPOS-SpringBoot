-- 1. Database အသစ်တည်ဆောက်ခြင်း
CREATE DATABASE IF NOT EXISTS CoffeePOSDB;
USE CoffeePOSDB;

-- 2. Categories Table
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Products Table
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
    -- Foreign Key Constraint
    CONSTRAINT FK_Product_Category FOREIGN KEY (category_id) 
    REFERENCES categories(category_id) ON DELETE SET NULL
);

-- 4. Sales Table
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

-- 5. SaleItems Table
CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    unit_price DECIMAL(18, 2) NOT NULL,
    quantity INT NOT NULL,
    sub_amount DECIMAL(18, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Foreign Key Constraints
    CONSTRAINT FK_SaleItem_Sale FOREIGN KEY (sale_id) 
    REFERENCES sales(sale_id) ON DELETE CASCADE,
    CONSTRAINT FK_SaleItem_Product FOREIGN KEY (product_id) 
    REFERENCES products(product_id) ON DELETE RESTRICT
);