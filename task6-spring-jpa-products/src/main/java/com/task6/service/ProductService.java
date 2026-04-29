package com.task6.service;

import com.task6.dto.ProductRequestDto;
import com.task6.dto.ProductResponseDto;
import com.task6.entity.Product;
import com.task6.entity.ProductType;
import com.task6.entity.User;
import com.task6.exception.ResourceNotFoundException;
import com.task6.repository.ProductRepository;
import com.task6.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductResponseDto> getProductsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Product> products = productRepository.findByUserIdAndIsActiveTrue(userId);
        return products.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return toResponseDto(product);
    }

    public ProductResponseDto getProductByAccountNumber(String accountNumber) {
        Product product = productRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with account number: " + accountNumber));
        return toResponseDto(product);
    }

    public List<ProductResponseDto> getProductsByUserIdAndType(Long userId, String type) {
        ProductType productType;
        try {
            productType = ProductType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product type. Must be CARD or ACCOUNT");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Product> products = productRepository.findByUserIdAndType(userId, productType);
        return products.stream()
                .filter(Product::getIsActive)
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (productRepository.existsByAccountNumber(requestDto.getAccountNumber())) {
            throw new IllegalArgumentException("Product with account number " + requestDto.getAccountNumber() + " already exists");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        Product product = new Product();
        product.setAccountNumber(requestDto.getAccountNumber());
        product.setBalance(requestDto.getBalance());
        product.setType(ProductType.valueOf(requestDto.getType().toUpperCase()));
        product.setUser(user);
        product.setIsActive(true);

        Product saved = productRepository.save(product);
        return toResponseDto(saved);
    }

    @Transactional
    public ProductResponseDto updateBalance(Long productId, BigDecimal newBalance) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        product.setBalance(newBalance);
        Product updated = productRepository.save(product);
        return toResponseDto(updated);
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public BigDecimal getTotalBalanceByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return productRepository.getTotalBalanceByUserId(userId);
    }

    public long getProductCountByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return productRepository.countByUserId(userId);
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