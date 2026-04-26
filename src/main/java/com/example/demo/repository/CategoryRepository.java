package com.example.demo.repository;

import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.category.CategoryUpdateRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private CategoryResponse mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoryId(rs.getInt("category_id"));
        categoryResponse.setCategoryCode(rs.getString("category_code"));
        categoryResponse.setCategoryName(rs.getString("category_name"));
        categoryResponse.setDescription(rs.getString("description"));
        return categoryResponse;
    }

    public Integer createCategory(String categoryCode, CategoryCreateRequest request) {
        String sql = """
                INSERT INTO categories (category_code, category_name, description)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, categoryCode);
            ps.setString(2, request.getCategoryName());
            ps.setString(3, request.getDescription());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public Integer getLastCategoryId() {
        String sql = "SELECT COALESCE(MAX(category_id), 0) FROM categories";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<CategoryResponse> getAllCategories(int page, int size) {
        int offset = page * size;
        String sql = """
                SELECT category_id, category_code, category_name, description
                FROM categories
                ORDER BY category_id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, size, offset);
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM categories";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public CategoryResponse getCategoryById(Integer id) {
        String sql = """
                SELECT category_id, category_code, category_name, description
                FROM categories
                WHERE category_id = ?
                """;
        List<CategoryResponse> result = jdbcTemplate.query(sql, this::mapRow, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public CategoryResponse getCategoryByCode(String code) {
        String sql = """
                SELECT category_id, category_code, category_name, description
                FROM categories
                WHERE category_code = ?
                """;
        List<CategoryResponse> result = jdbcTemplate.query(sql, this::mapRow, code);
        return result.isEmpty() ? null : result.get(0);
    }

    public int updateCategoryByCode(CategoryUpdateRequest request) {
        String sql = """
                UPDATE categories
                SET category_name = ?, description = ?
                WHERE category_code = ?
                """;
        return jdbcTemplate.update(sql,
                request.getCategoryName(),
                request.getDescription(),
                request.getCategoryCode());
    }

    public int deleteCategoryByCode(String code) {
        String sql = "DELETE FROM categories WHERE category_code = ?";
        return jdbcTemplate.update(sql, code);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }
}
