package com.example.demo.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SaleCreateRequest {

    // Used by the UPDATE endpoint — auto-generated in CREATE (ignored)
    private String saleCode;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @NotNull(message = "Net amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Net amount must be greater than 0")
    private BigDecimal netAmount;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotEmpty(message = "Sale must have at least one item")
    @Valid
    private List<SaleItemRequest> items;
}
