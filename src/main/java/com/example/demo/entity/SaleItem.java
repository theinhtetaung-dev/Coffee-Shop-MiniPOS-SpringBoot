package com.example.demo.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SaleItem {
        private Integer saleItemId;
        private Integer saleId;
        private Integer productId;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subAmount;
        private Timestamp createdAt;
        private Timestamp modifiedAt;
}
