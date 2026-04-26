package com.example.demo.controller;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.sale.SaleCodeRequest;
import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sale", description = "Manage sales transactions. Sale code is auto-generated as POS-{yyyyMMddHHmmss}.")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(
            summary = "Create a new sale",
            description = "Sale code is auto-generated. Provide customer info, amounts, payment type, and at least one item."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sale created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed — check request body")
    })
    @PostMapping
    public ResponseEntity<SaleResponse> createSale(
            @Valid @RequestBody SaleCreateRequest request) {
        SaleResponse response = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all sales (paginated)",
            description = "Returns a paginated list ordered by newest first. Default: page=0, size=10."
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of sales")
    @GetMapping
    public ResponseEntity<PagedResponse<SaleResponse>> getAllSales(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(saleService.getAllSales(page, size));
    }

    @Operation(
            summary = "Find sale by code",
            description = "Fetch a sale with its items. Provide { \"saleCode\": \"POS-20260426120000\" } in the request body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale found with items"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PostMapping("/find")
    public ResponseEntity<SaleResponse> getSaleByCode(
            @Valid @RequestBody SaleCodeRequest request) {
        return ResponseEntity.ok(saleService.getSaleByCode(request.getSaleCode()));
    }

    @Operation(
            summary = "Update sale",
            description = "Provide { \"saleCode\": \"POS-...\", \"customerName\": \"...\", ... } in the request body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed or sale code missing"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PutMapping
    public ResponseEntity<SaleResponse> updateSale(
            @Valid @RequestBody SaleCreateRequest request) {
        return ResponseEntity.ok(saleService.updateSale(request.getSaleCode(), request));
    }

    @Operation(
            summary = "Delete sale",
            description = "Provide { \"saleCode\": \"POS-20260426120000\" } in the request body to delete that sale."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale deleted"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteSale(
            @Valid @RequestBody SaleCodeRequest request) {
        saleService.deleteSale(request.getSaleCode());
        return ResponseEntity.ok("Sale [" + request.getSaleCode() + "] deleted successfully");
    }
}