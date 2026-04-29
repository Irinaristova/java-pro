package com.task7.payment.exception;

import com.task7.payment.dto.PaymentResponseDto;
import lombok.Getter;

@Getter
public class PaymentValidationException extends RuntimeException {
    private final PaymentResponseDto paymentResponse;

    public PaymentValidationException(String message) {
        super(message);
        this.paymentResponse = null;
    }

    public PaymentValidationException(String message, PaymentResponseDto paymentResponse) {
        super(message);
        this.paymentResponse = paymentResponse;
    }
}