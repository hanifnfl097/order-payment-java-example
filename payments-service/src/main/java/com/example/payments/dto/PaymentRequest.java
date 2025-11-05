package com.example.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body untuk membuat payment baru via POST /api/payments.
 * Nanti di STEP 5 bisa kita extend untuk menambah field "method" dll.
 */
public record PaymentRequest(
        @NotNull Long orderId,
        @NotNull @DecimalMin(value = "0.00") BigDecimal amount,
        @NotBlank String status
) {
}
