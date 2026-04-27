package com.example.demo.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductCreateRequest {
    private String productCode;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private Boolean isAvailable;
}
