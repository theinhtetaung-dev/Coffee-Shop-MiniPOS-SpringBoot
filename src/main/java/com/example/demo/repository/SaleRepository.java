package com.example.demo.repository;

import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleItemRequest;
import com.example.demo.dto.sale.SaleItemResponse;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.dto.sale.SaleUpdateRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SaleRepository {

    private final JdbcTemplate jdbcTemplate;

    public SaleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─── Helper: sale row mapper ──────────────────────────────────────────────
    private SaleResponse mapSaleRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        SaleResponse response = new SaleResponse();
        response.setSaleId(rs.getInt("sale_id"));
        response.setSaleCode(rs.getString("sale_code"));
        response.setCustomerName(rs.getString("customer_name"));
        response.setTotalAmount(rs.getBigDecimal("total_amount"));
        response.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        response.setNetAmount(rs.getBigDecimal("net_amount"));
        response.setPaymentType(rs.getString("payment_type"));
        return response;
    }

    // ─── Create ───────────────────────────────────────────────────────────────
    public Integer createSale(String saleCode, SaleCreateRequest request) {
        String sql = """
                INSERT INTO sales
                (sale_code, customer_name, total_amount, discount_amount, net_amount, payment_type)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, saleCode);
            ps.setString(2, request.getCustomerName());
            ps.setBigDecimal(3, request.getTotalAmount());
            ps.setBigDecimal(4, request.getDiscountAmount());
            ps.setBigDecimal(5, request.getNetAmount());
            ps.setString(6, request.getPaymentType());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void createSaleItems(Integer saleId, List<SaleItemRequest> items) {
        String sql = """
                INSERT INTO sale_items
                (sale_id, product_id, unit_price, quantity, sub_amount)
                VALUES (?, ?, ?, ?, ?)
                """;
        for (SaleItemRequest item : items) {
            jdbcTemplate.update(sql,
                    saleId,
                    item.getProductId(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getSubAmount());
        }
    }

    // ─── Get all (paginated) ──────────────────────────────────────────────────
    public List<SaleResponse> getAllSales(int page, int size) {
        int offset = page * size;
        String sql = """
                SELECT sale_id, sale_code, customer_name, total_amount, discount_amount,
                       net_amount, payment_type
                FROM sales
                ORDER BY sale_id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, this::mapSaleRow, size, offset);
    }

    // ─── Count total ──────────────────────────────────────────────────────────
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM sales";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    // ─── Get by ID ────────────────────────────────────────────────────────────
    public SaleResponse getSaleById(Integer saleId) {
        String sql = """
                SELECT sale_id, sale_code, customer_name, total_amount, discount_amount,
                       net_amount, payment_type
                FROM sales
                WHERE sale_id = ?
                """;
        List<SaleResponse> results = jdbcTemplate.query(sql, this::mapSaleRow, saleId);
        return results.isEmpty() ? null : results.get(0);
    }

    // ─── Get by Code ──────────────────────────────────────────────────────────
    public SaleResponse getSaleByCode(String saleCode) {
        String sql = """
                SELECT sale_id, sale_code, customer_name, total_amount, discount_amount,
                       net_amount, payment_type
                FROM sales
                WHERE sale_code = ?
                """;
        List<SaleResponse> results = jdbcTemplate.query(sql, this::mapSaleRow, saleCode);
        return results.isEmpty() ? null : results.get(0);
    }

    // ─── Get sale items ───────────────────────────────────────────────────────
    public List<SaleItemResponse> getSaleItemsBySaleId(Integer saleId) {
        String sql = """
                SELECT sale_item_id, product_id, unit_price, quantity, sub_amount
                FROM sale_items
                WHERE sale_id = ?
                ORDER BY sale_item_id ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SaleItemResponse item = new SaleItemResponse();
            item.setSaleItemId(rs.getInt("sale_item_id"));
            item.setProductId(rs.getInt("product_id"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));
            item.setQuantity(rs.getInt("quantity"));
            item.setSubAmount(rs.getBigDecimal("sub_amount"));
            return item;
        }, saleId);
    }

    public int updateSaleByCode(SaleUpdateRequest request) {
        String sql = """
                UPDATE sales
                SET customer_name = ?, total_amount = ?, discount_amount = ?,
                    net_amount = ?, payment_type = ?
                WHERE sale_code = ?
                """;
        return jdbcTemplate.update(sql,
                request.getCustomerName(),
                request.getTotalAmount(),
                request.getDiscountAmount(),
                request.getNetAmount(),
                request.getPaymentType(),
                request.getSaleCode());
    }

    public void deleteSaleItemsBySaleId(Integer saleId) {
        jdbcTemplate.update("DELETE FROM sale_items WHERE sale_id = ?", saleId);
    }

    public int deleteSaleByCode(String saleCode) {
        String sql = "DELETE FROM sales WHERE sale_code = ?";
        return jdbcTemplate.update(sql, saleCode);
    }

    public boolean existsById(Integer saleId) {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, saleId);
        return count != null && count > 0;
    }

    public boolean existsByCode(String saleCode) {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, saleCode);
        return count != null && count > 0;
    }
}