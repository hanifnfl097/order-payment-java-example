package com.example.payments.repo;

import com.example.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  /** Latest payment untuk order tertentu (dipakai /api/payments/order/{orderId}) */
  Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

  /**
   * Statistik jumlah pembayaran:
   * - count: jumlah record
   * - sum: total amount
   * - avg: rata-rata amount
   *
   * Filter waktu opsional: from / until (created_at).
   */
  @Query("""
            SELECT COUNT(p), COALESCE(SUM(p.amount), 0), COALESCE(AVG(p.amount), 0)
            FROM Payment p
            WHERE (:from IS NULL OR p.createdAt >= :from)
              AND (:until IS NULL OR p.createdAt <= :until)
            """)
  Object[] aggregateAmounts(@Param("from") OffsetDateTime from,
                            @Param("until") OffsetDateTime until);

  /**
   * Recent payments by status (native SQL), misal untuk dashboard.
   */
  @Query(value = """
            SELECT *
            FROM payments
            WHERE status = :status
            ORDER BY created_at DESC
            LIMIT :limit
            """,
          nativeQuery = true)
  List<Payment> recentByStatus(@Param("status") String status,
                               @Param("limit") int limit);
}
