package com.example.demo.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest extends CategoryCreateRequest {
        @NotBlank(message = "Category code is required")
        @Size(max = 20, message = "Category code must not exceed 20 characters")
        private String categoryCode;

}
