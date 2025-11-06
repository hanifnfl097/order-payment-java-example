package com.example.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long orderId,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotBlank String status,
        // method boleh null/empty, nanti di-controller dikasih default
        String method
) {
}
