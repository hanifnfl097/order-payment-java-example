package com.example.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // order_id BIGINT NOT NULL
  @Column(name = "order_id", nullable = false)
  private Long orderId;

  // amount NUMERIC(12,2) NOT NULL
  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  // status VARCHAR(20) NOT NULL
  @Column(name = "status", length = 20, nullable = false)
  private String status;

  // created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public Payment() {
  }

  // --- getters & setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
