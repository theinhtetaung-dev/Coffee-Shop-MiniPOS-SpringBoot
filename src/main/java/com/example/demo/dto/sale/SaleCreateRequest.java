package com.example.demo.dto.sale;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SaleCreateRequest {
        
        private String saleCode;
        private String customerName;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal netAmount;
        private String paymentType;
        private List<SaleItemRequest> items;
}
