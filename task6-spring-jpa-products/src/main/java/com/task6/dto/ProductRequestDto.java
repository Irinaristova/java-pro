package com.task6.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ProductRequestDto {

    @NotBlank(message = "Account number is required")
    @Size(min = 10, max = 50, message = "Account number must be between 10 and 50 characters")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotBlank(message = "Product type is required")
    @Pattern(regexp = "CARD|ACCOUNT", message = "Type must be CARD or ACCOUNT")
    private String type;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}