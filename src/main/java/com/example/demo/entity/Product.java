package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class Product {

    private Integer productId;
    private String productCode;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private Boolean isAvailable;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
}