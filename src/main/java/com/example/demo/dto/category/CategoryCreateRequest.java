package com.example.demo.dto.category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CategoryCreateRequest {
    private String categoryCode;
    private String categoryName;
    private String description;
}
