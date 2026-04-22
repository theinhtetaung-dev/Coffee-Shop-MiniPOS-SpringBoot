package com.example.demo.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sale {
        private Integer saleId;
        private String saleCode;
        private String customerName;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal netAmount;
        private String paymentType;
        private Timestamp createdAt;
        private Timestamp modifiedAt;
}
