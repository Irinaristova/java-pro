package com.task7.payment.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
}