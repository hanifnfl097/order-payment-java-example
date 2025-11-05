package com.example.payments.api;

import com.example.payments.dto.PaymentRequest;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

  // -------------------------------------------------------------------------
  // Basic CRUD-ish endpoints
  // -------------------------------------------------------------------------

  /** List semua payments (untuk debug / demo) */
  @GetMapping
  public List<Payment> findAll() {
    return repo.findAll();
  }

  /** Get payment by id */
  @GetMapping("/{id}")
  public ResponseEntity<Payment> findById(@PathVariable("id") Long id) {
    return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  /** Payment terbaru untuk sebuah order (dipakai service orders) */
  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentDto> latestForOrder(@PathVariable("orderId") Long orderId) {
    return repo.findTopByOrderIdOrderByCreatedAtDesc(orderId)
            .map(this::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Create payment (fake gateway).
   *
   * Untuk sekarang, kita terima:
   *  - orderId
   *  - amount
   *  - status (misal "PENDING"/"PAID"/"FAILED")
   *
   * Nanti di STEP 5 bisa kita kembangkan supaya status otomatis PAID, dll.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentDto create(@Valid @RequestBody PaymentRequest request) {
    Payment p = new Payment();
    p.setOrderId(request.orderId());
    p.setAmount(request.amount());
    p.setStatus(request.status());
    p.setCreatedAt(OffsetDateTime.now());

    Payment saved = repo.save(p);
    return toDto(saved);
  }

  // -------------------------------------------------------------------------
  // Stats & analytics
  // -------------------------------------------------------------------------

  /**
   * Endpoint lama: /api/payments/stats/amounts
   *
   * Mengembalikan:
   * {
   *   "count": 10,
   *   "sum":   1234.56,
   *   "avg":   123.45
   * }
   *
   * Parameter opsional:
   *  - from  (ISO date-time)
   *  - until (ISO date-time)
   */
  @GetMapping("/stats/amounts")
  public Map<String, Object> statsAmounts(
          @RequestParam(value = "from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          OffsetDateTime from,
          @RequestParam(value = "until", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          OffsetDateTime until
  ) {
    Object[] row = repo.aggregateAmounts(from, until);

    Map<String, Object> m = new HashMap<>();
    if (row == null || row.length < 3) {
      m.put("count", 0L);
      m.put("sum", 0.0);
      m.put("avg", 0.0);
      return m;
    }

    Number count = (Number) row[0];
    Number sum = (Number) row[1];
    Number avg = (Number) row[2];

    m.put("count", count == null ? 0L : count.longValue());
    // sum & avg tetap kita kirim sebagai double untuk kompatibilitas JSON lama
    m.put("sum", sum == null ? 0.0 : new BigDecimal(sum.toString()).doubleValue());
    m.put("avg", avg == null ? 0.0 : new BigDecimal(avg.toString()).doubleValue());

    return m;
  }

  /**
   * Recent payments by status (optional, berguna buat dashboard).
   * Contoh: GET /api/payments/recent?status=PAID&limit=5
   */
  @GetMapping("/recent")
  public List<PaymentDto> recentByStatus(
          @RequestParam("status") String status,
          @RequestParam(value = "limit", defaultValue = "5") int limit
  ) {
    return repo.recentByStatus(status, limit)
            .stream()
            .map(this::toDto)
            .toList();
  }

  // -------------------------------------------------------------------------
  // Mapper
  // -------------------------------------------------------------------------

  private PaymentDto toDto(Payment payment) {
    return new PaymentDto(
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getCreatedAt()
    );
  }

  /** DTO untuk response ke orders-service / frontend */
  public record PaymentDto(
          Long id,
          Long orderId,
          BigDecimal amount,
          String status,
          OffsetDateTime createdAt
  ) {
  }
}
