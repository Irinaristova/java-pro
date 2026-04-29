package com.task7.payment.controller;

import com.task7.payment.dto.PaymentRequestDto;
import com.task7.payment.dto.PaymentResponseDto;
import com.task7.payment.dto.ProductResponseDto;
import com.task7.payment.exception.PaymentValidationException;
import com.task7.payment.exception.ProductServiceException;
import com.task7.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/products/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> getUserProducts(@PathVariable Long userId) {
        log.info("GET /payments/products/user/{}", userId);
        List<ProductResponseDto> products = paymentService.getUserProducts(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        log.info("GET /payments/products/{}", productId);
        ProductResponseDto product = paymentService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        log.info("POST /payments/process - productId: {}, userId: {}, amount: {}",
                request.getProductId(), request.getUserId(), request.getAmount());

        PaymentResponseDto response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<PaymentResponseDto> getTransaction(@PathVariable Long transactionId) {
        log.info("GET /payments/transactions/{}", transactionId);
        PaymentResponseDto transaction = paymentService.getTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }
}