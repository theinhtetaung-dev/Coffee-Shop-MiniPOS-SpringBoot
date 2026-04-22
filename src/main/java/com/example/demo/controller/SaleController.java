package com.example.demo.controller;

import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public SaleResponse createSale(@RequestBody SaleCreateRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{saleId}")
    public SaleResponse getSaleDetail(@PathVariable Integer saleId) {
        return saleService.getSaleDetail(saleId);
    }

    @PutMapping("/{saleId}")
    public SaleResponse updateSale(@PathVariable Integer saleId,
                                   @RequestBody SaleCreateRequest request) {
        return saleService.updateSale(saleId, request);
    }

    @DeleteMapping("/{saleId}")
    public String deleteSale(@PathVariable Integer saleId) {
        saleService.deleteSale(saleId);
        return "Sale deleted successfully";
    }
}