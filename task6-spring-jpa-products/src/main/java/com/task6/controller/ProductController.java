package com.task6.controller;

import com.task6.dto.ProductRequestDto;
import com.task6.dto.ProductResponseDto;
import com.task6.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByUserId(@PathVariable Long userId) {
        List<ProductResponseDto> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ProductResponseDto> getProductByAccountNumber(@PathVariable String accountNumber) {
        ProductResponseDto product = productService.getProductByAccountNumber(accountNumber);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable String type) {
        List<ProductResponseDto> products = productService.getProductsByUserIdAndType(userId, type);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/{userId}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable Long userId) {
        BigDecimal totalBalance = productService.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok(totalBalance);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getProductCount(@PathVariable Long userId) {
        long count = productService.getProductCountByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto created = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}/balance")
    public ResponseEntity<ProductResponseDto> updateBalance(
            @PathVariable Long productId,
            @RequestParam BigDecimal balance) {
        ProductResponseDto updated = productService.updateBalance(productId, balance);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }
}