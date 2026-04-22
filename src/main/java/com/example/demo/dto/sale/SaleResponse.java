package com.example.demo.dto.sale;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleResponse {
        private Integer saleId;
        private String saleCode;
        private String customerName;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal netAmount;
        private String paymentType;
        private Timestamp createdAt;
        private List<SaleItemResponse> items;
}
