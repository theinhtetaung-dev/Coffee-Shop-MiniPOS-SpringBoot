package com.example.demo.repository;

import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductCreateRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Creating
    public Integer createProduct(ProductCreateRequest request) {
        String sql = """
                INSERT INTO products(product_code, product_name, description, price, category_id, is_available)
                VALUES(?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(sql,
                request.getProductCode(),
                request.getProductName(),
                request.getDescription(),
                request.getCategoryId(),
                request.getIsAvailable());
    }

    // Getting All
    public List<ProductResponse> getAllProducts() {
        String sql = """
                SELECT product_code, product_name, description, price, category_id, is_available, created_at
                FROM products
                ORDER BY product id DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProductResponse p = new ProductResponse();
            p.setProductId(rs.getInt("product_id"));
            p.setProductCode(rs.getString("product_code"));
            p.setProductName(rs.getString("product_name"));
            p.setDescription(rs.getString("description"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setIsAvailable(rs.getBoolean("is_available"));
            p.setCreatedAt(rs.getTimestamp("created_at"));
            return p;
        });
    }

    // Getting By Id
    public ProductResponse getProductById(Integer id) {
        String sql = """
                SELECT product_code, product_name, description, price, category_id, is_available, created_at
                FROM products
                WHERE id = ?
                """;
        List<ProductResponse> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProductResponse p = new ProductResponse();
            p.setProductId(rs.getInt("product_id"));
            p.setProductCode(rs.getString("product_code"));
            p.setProductName(rs.getString("product_name"));
            p.setDescription(rs.getString("description"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setIsAvailable(rs.getBoolean("is_available"));
            p.setCreatedAt(rs.getTimestamp("created_at"));
            return p;
        }, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Updating
    public int updateProduct(Integer id, ProductCreateRequest request) {
        String sql = """
                UPDATE products
                SET product_code = ?, product_name, description = ?, price = ?, category_id = ?, isAvailable = ?
                WHERE product_id = ?
                """;

        return jdbcTemplate.update(sql,
                request.getProductCode(),
                request.getProductName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategoryId(),
                request.getIsAvailable(),
                id);
    }

    // Deleting
    public int deleteProduct(Integer id) {
        String sql = """
                DELETE FROM products WHERE product_id = ?
                """;
        return jdbcTemplate.update(sql, id);
    }

    // EXISTS
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
