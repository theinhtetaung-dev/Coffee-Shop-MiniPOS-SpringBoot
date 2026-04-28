package com.example.demo.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductUpdateRequest extends ProductCreateRequest {
    @NotBlank(message = "Product code is required")
    private String productCode;

}
