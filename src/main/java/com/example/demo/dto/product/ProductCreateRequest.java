package com.example.demo.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {
    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private Boolean isAvailable;
}
