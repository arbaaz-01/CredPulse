package com.ofss.project.service;

import com.ofss.project.dto.response.CreditCardProductResponse;
import com.ofss.project.entity.CreditCardProduct;
import com.ofss.project.enums.CreditCardProductStatus;
import com.ofss.project.repository.CreditCardProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardProductService {

    private final CreditCardProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CreditCardProductResponse> getActiveProducts() {

        return productRepository
                .findByStatusOrderByNameAsc(
                        CreditCardProductStatus.ACTIVE
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditCardProductResponse getActiveProduct(
            Long productId) {

        CreditCardProduct product =
                productRepository
                        .findByIdAndStatus(
                                productId,
                                CreditCardProductStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Credit card product not found"
                                )
                        );

        return toResponse(product);
    }

    private CreditCardProductResponse toResponse(
            CreditCardProduct product) {

        return new CreditCardProductResponse(
                product.getId(),
                product.getProductCode(),
                product.getName(),
                product.getDescription(),
                product.getMinimumIncome(),
                product.getAnnualFee(),
                product.getInterestRate(),
                product.getMinCreditLimit(),
                product.getMaxCreditLimit(),
                product.getStatus()
        );
    }
}