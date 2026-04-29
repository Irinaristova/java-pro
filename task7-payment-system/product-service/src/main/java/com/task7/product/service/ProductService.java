package com.task7.product.service;

import com.task7.product.dto.ProductResponseDto;
import com.task7.product.entity.Product;
import com.task7.product.exception.InsufficientBalanceException;
import com.task7.product.exception.ResourceNotFoundException;
import com.task7.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto> getProductsByUserId(Long userId) {
        List<Product> products = productRepository.findByUserIdAndIsActiveTrue(userId);
        return products.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return toResponseDto(product);
    }

    public ProductResponseDto getProductByAccountNumber(String accountNumber) {
        Product product = productRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + accountNumber));
        return toResponseDto(product);
    }

    public boolean hasSufficientBalance(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return product.getBalance().compareTo(amount) >= 0;
    }

    @Transactional
    public ProductResponseDto withdraw(Long productId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is not active");
        }

        if (product.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Required: %.2f, Available: %.2f",
                            amount, product.getBalance())
            );
        }

        product.setBalance(product.getBalance().subtract(amount));
        Product saved = productRepository.save(product);
        return toResponseDto(saved);
    }

    @Transactional
    public ProductResponseDto deposit(Long productId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        product.setBalance(product.getBalance().add(amount));
        Product saved = productRepository.save(product);
        return toResponseDto(saved);
    }

    private ProductResponseDto toResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .accountNumber(product.getAccountNumber())
                .balance(product.getBalance())
                .type(product.getType().name())
                .typeDescription(product.getType().getDescription())
                .userId(product.getUser().getId())
                .username(product.getUser().getUsername())
                .isActive(product.getIsActive())
                .build();
    }
}