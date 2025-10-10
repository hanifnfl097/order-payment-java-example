
package com.example.orders.dto;
import java.time.OffsetDateTime; import java.util.List;
public record OrderWithPaymentDTO(Long orderId, String userName, OffsetDateTime createdAt, double totalAmount, Payment payment, List<Line> items){
  public record Payment(Long id, Long orderId, Double amount, String status, String createdAt) { }
  public record Line(String productName, double unitPrice, int quantity) { }
}
