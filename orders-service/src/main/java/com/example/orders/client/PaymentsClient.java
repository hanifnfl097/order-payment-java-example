package com.example.orders.client;

import com.example.orders.dto.OrderWithPaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Payments client using Spring's RestClient + Jackson mapping.
 * - No manual JSON parsing
 * - Base URL is configurable via property or env
 *
 * Property resolution:
 *   payments.base-url in application.properties
 *   or environment variable PAYMENTS_BASE_URL (Spring relaxed binding)
 */
@Component
public class PaymentsClient {

  private final RestClient rest;

  // Prefer "payments.base-url" (env var PAYMENTS_BASE_URL also works)
  public PaymentsClient(
          RestClient.Builder builder,
          @Value("${payments.base-url:http://localhost:8082}") String baseUrl
  ) {
    this.rest = builder
            .baseUrl(baseUrl)
            .build();
  }

  public OrderWithPaymentDTO.Payment getPaymentByOrderId(Long orderId) {
    PaymentDto dto = rest.get()
            .uri("/api/payments/order/{id}", orderId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PaymentDto.class);

    if (dto == null) return null;

    return new OrderWithPaymentDTO.Payment(
            dto.id(),
            dto.orderId(),
            dto.amount(),
            dto.status(),
            dto.createdAt()
    );
  }

  /** Local DTO for JSON mapping from payments-service */
  public record PaymentDto(
          Long id,
          Long orderId,
          Double amount,
          String status,
          String createdAt
  ) {}
}
