
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
@RestController @RequestMapping("/api/payments")
public class PaymentController {
  private final PaymentRepository repo;
  public PaymentController(PaymentRepository repo){ this.repo = repo; }
  @GetMapping public List<Payment> all(){ return repo.findAll(); }
  @PostMapping public Payment create(@RequestBody PaymentRequest req){
    Payment p = new Payment(); p.setOrderId(req.orderId()); p.setAmount(req.amount()); p.setStatus(req.status()); return repo.save(p);
  }
  @GetMapping("/order/{orderId}") public ResponseEntity<Payment> byOrder(@PathVariable Long orderId){
    return repo.findByOrderId(orderId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
  @GetMapping("/stats/amounts")
  public Map<String,Object> stats(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
                                  @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime until){
    Object row = repo.statsBetween(since, until);
    Object[] a = (Object[]) row;
    Map<String,Object> m = new HashMap<>();
    m.put("count", ((Number)a[0]).longValue());
    m.put("sum", ((Number)a[1]).doubleValue());
    m.put("avg", ((Number)a[2]).doubleValue());
    return m;
  }
}
