package com.example.demo.repository;

import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductUpdateRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ProductResponse mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ProductResponse p = new ProductResponse();
        p.setProductId(rs.getInt("product_id"));
        p.setProductCode(rs.getString("product_code"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setIsAvailable(rs.getBoolean("is_available"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    public Integer createProduct(String productCode, ProductCreateRequest request) {
        String sql = """
                INSERT INTO products(product_code, product_name, description, price, category_id, is_available)
                VALUES(?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, productCode);
            ps.setString(2, request.getProductName());
            ps.setString(3, request.getDescription());
            ps.setBigDecimal(4, request.getPrice());
            ps.setInt(5, request.getCategoryId());
            ps.setBoolean(6, request.getIsAvailable() != null ? request.getIsAvailable() : true);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public Integer getLastProductId() {
        String sql = "SELECT COALESCE(MAX(product_id), 0) FROM products";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<ProductResponse> getAllProducts(int page, int size) {
        int offset = page * size;
        String sql = """
                SELECT product_id, product_code, product_name, description, price, category_id, is_available, created_at
                FROM products
                ORDER BY product_id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, size, offset);
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public ProductResponse getProductById(Integer id) {
        String sql = """
                SELECT product_id, product_code, product_name, description, price, category_id, is_available, created_at
                FROM products
                WHERE product_id = ?
                """;
        List<ProductResponse> list = jdbcTemplate.query(sql, this::mapRow, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public ProductResponse getProductByCode(String code) {
        String sql = """
                SELECT product_id, product_code, product_name, description, price, category_id, is_available, created_at
                FROM products
                WHERE product_code = ?
                """;
        List<ProductResponse> list = jdbcTemplate.query(sql, this::mapRow, code);
        return list.isEmpty() ? null : list.get(0);
    }

    public int updateProductByCode(ProductUpdateRequest request) {
        String sql = """
                UPDATE products
                SET product_name = ?, description = ?, price = ?, category_id = ?, is_available = ?
                WHERE product_code = ?
                """;

        return jdbcTemplate.update(sql,
                request.getProductName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategoryId(),
                request.getIsAvailable() != null ? request.getIsAvailable() : true,
                request.getProductCode());
    }

    public int deleteProductByCode(String code) {
        String sql = "DELETE FROM products WHERE product_code = ?";
        return jdbcTemplate.update(sql, code);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }
}
