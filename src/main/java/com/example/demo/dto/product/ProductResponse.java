package com.example.demo.dto.product;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class ProductResponse {
    private Integer productId;
    private String productCode;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private Boolean isAvailable;
    private Timestamp createdAt;
}
