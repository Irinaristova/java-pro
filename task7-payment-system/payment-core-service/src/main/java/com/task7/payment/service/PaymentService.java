package com.task7.payment.service;

import com.task7.payment.client.ProductServiceClient;
import com.task7.payment.dto.PaymentRequestDto;
import com.task7.payment.dto.PaymentResponseDto;
import com.task7.payment.dto.ProductResponseDto;
import com.task7.payment.exception.PaymentValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductServiceClient productServiceClient;

    private final Map<Long, PaymentResponseDto> transactions = new ConcurrentHashMap<>();
    private final AtomicLong transactionIdGenerator = new AtomicLong(1);

    public List<ProductResponseDto> getUserProducts(Long userId) {
        log.info("Requesting products for user: {}", userId);
        return productServiceClient.getProductsByUserId(userId);
    }

    public ProductResponseDto getProduct(Long productId) {
        log.info("Requesting product by ID: {}", productId);
        ProductResponseDto product = productServiceClient.getProductById(productId);
     
        if (product.getIsActive() == null || !product.getIsActive()) {
            throw new PaymentValidationException("Product is not active");
        }
        
        return product;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        log.info("Processing payment for product: {}, amount: {}", request.getProductId(), request.getAmount());

        String transactionId = UUID.randomUUID().toString();
        PaymentResponseDto.PaymentResponseDtoBuilder responseBuilder = PaymentResponseDto.builder()
                .productId(request.getProductId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .timestamp(LocalDateTime.now());

        try {
            ProductResponseDto product = productServiceClient.getProductById(request.getProductId());

            if (!product.getUserId().equals(request.getUserId())) {
                log.warn("Product {} does not belong to user {}", request.getProductId(), request.getUserId());
                throw new PaymentValidationException("Product does not belong to the user");
            }

            if (!product.getIsActive()) {
                throw new PaymentValidationException("Product is not active");
            }

            boolean sufficient = productServiceClient.hasSufficientBalance(request.getProductId(), request.getAmount());
            if (!sufficient) {
                log.warn("Insufficient balance for product: {}, balance: {}", 
                        request.getProductId(), product.getBalance());
                throw new PaymentValidationException(
                        String.format("Insufficient balance. Required: %.2f, Available: %.2f",
                                request.getAmount(), product.getBalance())
                );
            }

            ProductResponseDto updatedProduct = productServiceClient.withdraw(request.getProductId(), request.getAmount());

            PaymentResponseDto response = responseBuilder
                    .transactionId(transactionIdGenerator.getAndIncrement())
                    .accountNumber(updatedProduct.getAccountNumber())
                    .username(updatedProduct.getUsername())
                    .newBalance(updatedProduct.getBalance())
                    .status("SUCCESS")
                    .message("Payment processed successfully")
                    .build();

            transactions.put(response.getTransactionId(), response);
            log.info("Payment completed successfully. Transaction: {}", response.getTransactionId());

            return response;

        } catch (PaymentValidationException e) {
            log.error("Payment validation failed: {}", e.getMessage());
            PaymentResponseDto response = responseBuilder
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
            throw new PaymentValidationException(e.getMessage(), response);

        } catch (com.task7.payment.exception.ProductServiceException e) {
            log.error("Product Service error: {}", e.getMessage());
            PaymentResponseDto response = responseBuilder
                    .status("FAILED")
                    .message("Product Service error: " + e.getMessage())
                    .build();
            throw new com.task7.payment.exception.ProductServiceException(e.getMessage(), response, e.getStatus());

        } catch (Exception e) {
            log.error("Unexpected error during payment: {}", e.getMessage(), e);
            PaymentResponseDto response = responseBuilder
                    .status("ERROR")
                    .message("Internal payment error: " + e.getMessage())
                    .build();
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    public PaymentResponseDto getTransaction(Long transactionId) {
        PaymentResponseDto transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new PaymentValidationException("Transaction not found: " + transactionId);
        }
        return transaction;
    }
}