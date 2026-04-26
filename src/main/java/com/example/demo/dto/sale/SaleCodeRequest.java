package com.example.demo.dto.sale;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleCodeRequest {

    @NotBlank(message = "Sale code is required")
    private String saleCode;
}
