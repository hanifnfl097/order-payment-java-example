
package com.example.payments.dto;
public record PaymentRequest(Long orderId, Double amount, String status) { }
