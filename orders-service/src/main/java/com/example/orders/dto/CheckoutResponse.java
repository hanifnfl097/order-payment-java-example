package com.example.orders.dto;

import com.example.orders.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CheckoutResponse(
        Long orderId,
        Long userId,
        String shippingAddress,
        String contactPhone,
        OrderStatus status,
        BigDecimal totalAmount,
        Long paymentId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<Line> items
) {
    public record Line(
            Long productId,
            String productName,
            int quantity
    ) { }
}
