package com.example.payments.api;

import com.example.payments.enums.PaymentStatus;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // ========= GET All Payments (dengan filter & paging) =========
    // GET /api/payments?status=PAID&method=BANK_TRANSFER&page=0&size=10
    @GetMapping
    public Page<Payment> listPayments(
            @RequestParam(name = "status", required = false) PaymentStatus status,
            @RequestParam(name = "method", required = false) String method,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // kalau from / to tidak diisi, pakai range sangat lebar
        LocalDateTime fromDate = (from != null)
                ? from
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        LocalDateTime toDate = (to != null)
                ? to
                : LocalDateTime.of(2100, 1, 1, 0, 0);

        return paymentRepository.searchPayments(status, method, fromDate, toDate, pageable);
    }

    // ========= GET Payment By ID =========
    // GET /api/payments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") Long id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ========= GET Payment By Order ID =========
    // GET /api/payments/order/{orderId}
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable("orderId") Long orderId) {
        return paymentRepository.findFirstByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ========= Create Payment =========
    // POST /api/payments
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment request) {
        // Biasanya status default PAID untuk contoh mini-project ini
        if (request.getStatus() == null) {
            request.setStatus(PaymentStatus.PAID);
        }

        request.setId(null); // pastikan dianggap insert, bukan update
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ========= Get Stats Amounts =========
    // GET /api/payments/stats/amounts
    @GetMapping("/stats/amounts")
    public Map<String, BigDecimal> getStatsAmounts() {
        LocalDateTime now = LocalDateTime.now();

        // Hari ini (mulai jam 00:00 sampai sekarang)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        BigDecimal today = paymentRepository.totalPaidAmountBetween(
                PaymentStatus.PAID, todayStart, now);

        // 7 hari terakhir
        BigDecimal last7Days = paymentRepository.totalPaidAmountBetween(
                PaymentStatus.PAID, now.minusDays(7), now);

        // 30 hari terakhir
        BigDecimal last30Days = paymentRepository.totalPaidAmountBetween(
                PaymentStatus.PAID, now.minusDays(30), now);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("today", today);
        result.put("last7Days", last7Days);
        result.put("last30Days", last30Days);

        return result;
    }

    // ========= Get Recent Payments =========
    // GET /api/payments/recent
    @GetMapping("/recent")
    public List<Payment> getRecentPayments() {
        return paymentRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
