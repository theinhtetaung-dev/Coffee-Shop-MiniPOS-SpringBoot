package com.example.demo.dto.category;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter

public class CategoryResponse {
    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Timestamp createdAt;
}

