package com.task7.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String type;
    private String typeDescription;
    private Long userId;
    private String username;
    private Boolean isActive;
}