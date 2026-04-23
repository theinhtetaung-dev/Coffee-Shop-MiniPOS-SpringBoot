package com.example.demo.repository;

import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.category.CategoryCreateRequest;
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

    public CategoryRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //Creating
    public Integer createCategory(CategoryCreateRequest request){
        String sql = """
                INSERT INTO categories (category_code, category_name, description)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getCategoryCode());
            ps.setString(2, request.getCategoryName());
            ps.setString(3, request.getDescription());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    //Getting_all
    public List<CategoryResponse> getAllCategories(){
        String sql = """
                SELECT category_id, category_code, category_name, description, created_at
                FROM categories
                ORDER BY category_id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CategoryResponse c = new CategoryResponse();
            c.setCategoryId(rs.getInt("category_id"));
            c.setCategoryCode(rs.getString("category_code"));
            c.setCategoryName(rs.getString("category_name"));
            c.setDescription(rs.getString("description"));
            c.setCreatedAt(rs.getTimestamp("created_at"));
            return c;
        });
    }

    //Getting_using_Id
    public CategoryResponse getCategoryById(Integer id){
        String sql = """
                SELECT category_id, category_code, category_name, descrption, created_at
                FROM categories
                WHERE category_id = ?
                """;
        List<CategoryResponse> result = jdbcTemplate.query(sql, (rs, rowNum ) -> {
            CategoryResponse c = new CategoryResponse();
            c.setCategoryId(rs.getInt("category_id"));
            c.setCategoryCode(rs.getString("category_code"));
            c.setCategoryName(rs.getString("category_name"));
            c.setDescription(rs.getString("description"));
            c.setCreatedAt(rs.getTimestamp("created_at"));
            return c;
        }, id);

        return result.isEmpty() ? null : result.get(0);
    }

    //Updating
    public int updateCategory(Integer id, CategoryCreateRequest request){
        String sql = """
                UPDATE categories
                SET category_code = ?, category_name = ?, description = ?
                WHERE category_id = ?
                """;

        return jdbcTemplate.update(sql,
                request.getCategoryCode(),
                request.getCategoryName(),
                request.getDescription(), id);
    }

    //Deleting
    public int deleteCategory(Integer id){
        String sql = """
                DELETE FROM categories WHERE category_id = ?
                """;
        return jdbcTemplate.update(sql, id);
    }

    //Checking_Existence
    public boolean existsById(Integer id){
        String sql = "SELECT COUNT(*) FROM categories WHERE category_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
