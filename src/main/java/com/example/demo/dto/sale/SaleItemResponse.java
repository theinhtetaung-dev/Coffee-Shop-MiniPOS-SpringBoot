package com.example.demo.dto.sale;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SaleItemResponse {
        private Integer saleItemId;
        private Integer productId;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subAmount;

}
