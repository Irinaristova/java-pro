package com.task7.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {
    private Long transactionId;
    private Long productId;
    private String accountNumber;
    private Long userId;
    private String username;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String description;
}