package com.example.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CheckoutRequest(
        @NotNull Long userId,
        @NotEmpty List<Item> items,
        @NotBlank String shippingAddress,
        @NotBlank String contactPhone
) {
    public record Item(
            @NotNull Long productId,
            @Positive int quantity
    ) { }
}
