package com.example.payments.repo;

import com.example.payments.model.Payment;
import com.example.payments.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // List payments dengan filter optional + paging
    @Query("""
           select p
           from Payment p
           where (:status is null or p.status = :status)
             and (:method is null or p.method = :method)
             and p.createdAt between :from and :to
           """)
    Page<Payment> searchPayments(
            @Param("status") PaymentStatus status,
            @Param("method") String method,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    // Cari payment berdasarkan orderId (biasanya 1 order 1 payment)
    Optional<Payment> findFirstByOrderId(Long orderId);

    // Total amount payment (biasanya hanya PAID) dalam rentang tanggal
    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.status = :status
             and p.createdAt between :from and :to
           """)
    BigDecimal totalPaidAmountBetween(
            @Param("status") PaymentStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // 5 payment terbaru
    List<Payment> findTop5ByOrderByCreatedAtDesc();
}
