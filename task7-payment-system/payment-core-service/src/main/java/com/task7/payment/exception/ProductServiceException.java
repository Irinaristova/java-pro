package com.task7.payment.exception;

import com.task7.payment.dto.PaymentResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductServiceException extends RuntimeException {
    private final HttpStatus status;
    private final PaymentResponseDto paymentResponse;

    public ProductServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.paymentResponse = null;
    }

    public ProductServiceException(String message, PaymentResponseDto paymentResponse, HttpStatus status) {
        super(message);
        this.paymentResponse = paymentResponse;
        this.status = status;
    }
}