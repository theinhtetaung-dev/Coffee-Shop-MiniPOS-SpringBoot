package com.example.demo.controller;

import com.example.demo.dto.PagedResponse;
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
            description = "Sale code is auto-generated as 'POS-{yyyyMMddHHmmss}'. Provide customer info, amounts, payment type, and at least one item."
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
            description = "Returns a paginated list of sales ordered by newest first. Default: page=0, size=10."
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
            summary = "Get sale by code",
            description = "Fetch a single sale with its items using the auto-generated sale code, e.g. 'POS-20260425231207'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale found with items"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/code/{saleCode}")
    public ResponseEntity<SaleResponse> getSaleByCode(
            @Parameter(description = "Sale code, e.g. POS-20260425231207", example = "POS-20260425231207")
            @PathVariable String saleCode) {
        return ResponseEntity.ok(saleService.getSaleByCode(saleCode));
    }

    @Operation(
            summary = "Update sale by code",
            description = "Update sale details and replace all sale items. The sale code itself never changes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PutMapping("/code/{saleCode}")
    public ResponseEntity<SaleResponse> updateSale(
            @Parameter(description = "Sale code, e.g. POS-20260425231207", example = "POS-20260425231207")
            @PathVariable String saleCode,
            @Valid @RequestBody SaleCreateRequest request) {
        return ResponseEntity.ok(saleService.updateSale(saleCode, request));
    }

    @Operation(
            summary = "Delete sale by code",
            description = "Permanently delete a sale and all its items using the sale code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sale deleted"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @DeleteMapping("/code/{saleCode}")
    public ResponseEntity<String> deleteSale(
            @Parameter(description = "Sale code, e.g. POS-20260425231207", example = "POS-20260425231207")
            @PathVariable String saleCode) {
        saleService.deleteSale(saleCode);
        return ResponseEntity.ok("Sale [" + saleCode + "] deleted successfully");
    }
}