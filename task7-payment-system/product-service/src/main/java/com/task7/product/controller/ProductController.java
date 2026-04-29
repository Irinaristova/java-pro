package com.task7.product.controller;

import com.task7.product.dto.ProductResponseDto;
import com.task7.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(productService.getProductsByUserId(userId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ProductResponseDto> getProductByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(productService.getProductByAccountNumber(accountNumber));
    }

    @GetMapping("/{productId}/balance/sufficient")
    public ResponseEntity<Boolean> hasSufficientBalance(
            @PathVariable Long productId,
            @RequestParam BigDecimal amount) {
        boolean sufficient = productService.hasSufficientBalance(productId, amount);
        return ResponseEntity.ok(sufficient);
    }

    @PostMapping("/{productId}/withdraw")
    public ResponseEntity<ProductResponseDto> withdraw(
            @PathVariable Long productId,
            @RequestParam BigDecimal amount) {
        ProductResponseDto updated = productService.withdraw(productId, amount);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{productId}/deposit")
    public ResponseEntity<ProductResponseDto> deposit(
            @PathVariable Long productId,
            @RequestParam BigDecimal amount) {
        ProductResponseDto updated = productService.deposit(productId, amount);
        return ResponseEntity.ok(updated);
    }
}