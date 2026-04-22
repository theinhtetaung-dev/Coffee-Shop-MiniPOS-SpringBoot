package com.example.demo.service.impl;

import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.repository.SaleRepository;
import com.example.demo.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    public SaleServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleCreateRequest request) {
        Integer saleId = saleRepository.createSale(request);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            saleRepository.createSaleItems(saleId, request.getItems());
        }

        return getSaleDetail(saleId);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return saleRepository.getAllSales();
    }

    @Override
    public SaleResponse getSaleDetail(Integer saleId) {
        SaleResponse sale = saleRepository.getSaleById(saleId);

        if (sale == null) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }

        sale.setItems(saleRepository.getSaleItemsBySaleId(saleId));
        return sale;
    }

    @Override
    @Transactional
    public SaleResponse updateSale(Integer saleId, SaleCreateRequest request) {
        if (!saleRepository.existsById(saleId)) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }

        saleRepository.updateSale(saleId, request);

        saleRepository.deleteSaleItemsBySaleId(saleId);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            saleRepository.createSaleItems(saleId, request.getItems());
        }

        return getSaleDetail(saleId);
    }

    @Override
    @Transactional
    public void deleteSale(Integer saleId) {
        if (!saleRepository.existsById(saleId)) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }

        saleRepository.deleteSaleItemsBySaleId(saleId);
        saleRepository.deleteSale(saleId);
    }
}