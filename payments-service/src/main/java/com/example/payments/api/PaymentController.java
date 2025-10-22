package com.example.payments.api;

import com.example.payments.dto.PaymentRequest;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentRepository repo;

  public PaymentController(PaymentRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Payment> all() {
    // keep entity listing for demo simplicity
    return repo.findAll();
  }

  @PostMapping
  public Payment create(@RequestBody PaymentRequest req) {
    Payment p = new Payment();
    p.setOrderId(req.orderId());
    p.setAmount(req.amount());
    p.setStatus(req.status());
    return repo.save(p);
  }

  /** ✅ Return 200 with DTO or 404 (no 500) */
  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentDto> byOrder(@PathVariable("orderId") Long orderId) {
    return repo.findTopByOrderIdOrderByCreatedAtDesc(orderId)
            .map(p -> ResponseEntity.ok(new PaymentDto(
                    p.getId(),
                    p.getOrderId(),
                    p.getAmount(),
                    p.getStatus(),
                    p.getCreatedAt()
            )))
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /** SQL aggregation with null-safe handling */
  @GetMapping("/stats/amounts")
  public Map<String, Object> stats(
          @RequestParam(name = "since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
          @RequestParam(name = "until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime until) {

    Object row = repo.statsBetween(since, until);
    Map<String, Object> m = new HashMap<>();
    if (row == null) {
      m.put("count", 0L);
      m.put("sum", 0.0);
      m.put("avg", 0.0);
      return m;
    }
    Object[] a = (Object[]) row;
    m.put("count", a[0] == null ? 0L : ((Number) a[0]).longValue());
    m.put("sum",   a[1] == null ? 0.0 : ((Number) a[1]).doubleValue());
    m.put("avg",   a[2] == null ? 0.0 : ((Number) a[2]).doubleValue());
    return m;
  }

  /** Local DTO for responses to orders-service */
  public record PaymentDto(Long id, Long orderId, Double amount, String status, OffsetDateTime createdAt) {}
}
