package com.example.demo.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCodeRequest {

    @NotBlank(message = "Category code is required")
    private String categoryCode;
}
