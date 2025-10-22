package com.example.payments.repo;

import com.example.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  /** Latest payment for a given order (used by /api/payments/order/{orderId}) */
  Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

  /** Aggregation example: count/sum/avg over a time window (native SQL) */
  @Query(value = """
            SELECT COUNT(*) AS cnt,
                   COALESCE(SUM(amount), 0) AS total,
                   COALESCE(AVG(amount), 0) AS avg
            FROM payments
            WHERE created_at BETWEEN :since AND :until
            """,
          nativeQuery = true)
  Object statsBetween(@Param("since") OffsetDateTime since,
                      @Param("until") OffsetDateTime until);

  /** Recent payments by status (native SQL) */
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
