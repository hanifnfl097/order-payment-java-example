package com.example.orders.api;

import com.example.orders.client.PaymentsClient;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderWithPaymentDTO;
import com.example.orders.model.Order;
import com.example.orders.repo.*;
import com.example.orders.service.OrderAnalyticsService;
import com.example.orders.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

  private final OrderService orderService;
  private final OrderRepository orderRepo;
  private final OrderItemRepository itemRepo;
  private final ProductRepository productRepo;
  private final UserRepository userRepo;
  private final OrderAnalyticsService analytics;
  private final PaymentsClient paymentsClient;

  public OrderController(
          OrderService s,
          OrderRepository o,
          OrderItemRepository i,
          ProductRepository p,
          UserRepository u,
          OrderAnalyticsService a,
          PaymentsClient pc
  ) {
    this.orderService = s;
    this.orderRepo = o;
    this.itemRepo = i;
    this.productRepo = p;
    this.userRepo = u;
    this.analytics = a;
    this.paymentsClient = pc;
  }

  @PostMapping("/orders")
  public Order createOrder(@RequestBody CreateOrderRequest req) {
    return orderService.create(req);
  }

  @GetMapping("/orders")
  public List<Order> orders() {
    return orderRepo.findAll();
  }

  @GetMapping("/users/{userId}/orders/history")
  public List<Map<String, Object>> history(@PathVariable("userId") Long userId) {
    return orderRepo.orderHistory(userId)
            .stream()
            .map(r -> {
              Object[] a = (Object[]) r;
              return Map.of(
                      "orderId", a[0],
                      "userName", a[1],
                      "createdAt", a[2],
                      "totalAmount", a[3]
              );
            })
            .collect(Collectors.toList());
  }

  @GetMapping("/reports/top-products")
  public List<Map<String, Object>> topProducts(@RequestParam(name = "limit", defaultValue = "5") int limit) {
    return itemRepo.topProducts(limit)
            .stream()
            .map(r -> {
              Object[] a = (Object[]) r;
              return Map.of("product", a[0], "qty", a[1]);
            })
            .collect(Collectors.toList());
  }

  @GetMapping("/orders/{orderId}/with-payment")
  public OrderWithPaymentDTO orderWithPayment(@PathVariable("orderId") Long orderId) {
    var order = orderRepo.findById(orderId).orElseThrow();
    var user = order.getUser();

    var items = itemRepo.findAll().stream()
            .filter(i -> i.getOrder().getId().equals(orderId))
            .toList();

    // ✅ Use BigDecimal instead of double to match DB precision
    BigDecimal total = items.stream()
            .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    var payment = paymentsClient.getPaymentByOrderId(orderId);

    return new OrderWithPaymentDTO(
            order.getId(),
            user.getName(),
            order.getCreatedAt(),
            total.doubleValue(), // safely convert BigDecimal → double for DTO display
            payment,
            items.stream()
                    .map(i -> new OrderWithPaymentDTO.Line(
                            i.getProduct().getName(),
                            i.getProduct().getPrice().doubleValue(),
                            i.getQuantity()
                    ))
                    .toList()
    );
  }

  @GetMapping("/orders/stream/large")
  public List<Map<String, Object>> largeOrders(@RequestParam(name = "minTotal", defaultValue = "100.0") double minTotal) {
    return analytics.findLargeOrders(minTotal);
  }
}
