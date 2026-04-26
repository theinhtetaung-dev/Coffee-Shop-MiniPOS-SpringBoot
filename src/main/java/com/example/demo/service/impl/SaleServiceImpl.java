package com.example.demo.service.impl;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.repository.SaleRepository;
import com.example.demo.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    private static final DateTimeFormatter CODE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public SaleServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleCreateRequest request) {
        String saleCode = "POS-" + LocalDateTime.now().format(CODE_FORMATTER);

        Integer saleId = saleRepository.createSale(saleCode, request);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            saleRepository.createSaleItems(saleId, request.getItems());
        }

        return buildSaleDetail(saleRepository.getSaleById(saleId));
    }

    @Override
    public PagedResponse<SaleResponse> getAllSales(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page index must not be less than zero");
        if (size < 1) throw new IllegalArgumentException("Page size must not be less than one");

        List<SaleResponse> data = saleRepository.getAllSales(page, size);
        long total = saleRepository.countAll();
        return new PagedResponse<>(data, page, size, total);
    }

    @Override
    public SaleResponse getSaleByCode(String saleCode) {
        if (saleCode == null || saleCode.isBlank()) {
            throw new IllegalArgumentException("Sale code must not be blank");
        }
        SaleResponse sale = saleRepository.getSaleByCode(saleCode);
        if (sale == null) {
            throw new RuntimeException("Sale not found with code: " + saleCode);
        }
        return buildSaleDetail(sale);
    }

    @Override
    @Transactional
    public SaleResponse updateSale(String saleCode, SaleCreateRequest request) {
        if (saleCode == null || saleCode.isBlank()) {
            throw new IllegalArgumentException("Sale code must not be blank");
        }
        SaleResponse existing = saleRepository.getSaleByCode(saleCode);
        if (existing == null) {
            throw new RuntimeException("Sale not found with code: " + saleCode);
        }

        saleRepository.updateSaleByCode(saleCode, request);

        // Replace sale items
        saleRepository.deleteSaleItemsBySaleId(existing.getSaleId());
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            saleRepository.createSaleItems(existing.getSaleId(), request.getItems());
        }

        return buildSaleDetail(saleRepository.getSaleByCode(saleCode));
    }

    @Override
    @Transactional
    public void deleteSale(String saleCode) {
        if (saleCode == null || saleCode.isBlank()) {
            throw new IllegalArgumentException("Sale code must not be blank");
        }
        SaleResponse existing = saleRepository.getSaleByCode(saleCode);
        if (existing == null) {
            throw new RuntimeException("Sale not found with code: " + saleCode);
        }
        saleRepository.deleteSaleItemsBySaleId(existing.getSaleId());
        saleRepository.deleteSaleByCode(saleCode);
    }

    private SaleResponse buildSaleDetail(SaleResponse sale) {
        sale.setItems(saleRepository.getSaleItemsBySaleId(sale.getSaleId()));
        return sale;
    }
}