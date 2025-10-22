package com.example.orders.service;

import com.example.orders.repo.OrderItemRepository;
import com.example.orders.repo.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderAnalyticsService {

  private final OrderRepository orderRepo;
  private final OrderItemRepository itemRepo;

  public OrderAnalyticsService(OrderRepository o, OrderItemRepository i) {
    this.orderRepo = o;
    this.itemRepo = i;
  }

  /**
   * Streams-based in-memory analytics:
   * - Group items by order
   * - Sum totals using BigDecimal (price * qty)
   * - Filter by minTotal
   * - Sort by total desc
   */
  public List<Map<String, Object>> findLargeOrders(double minTotal) {
    var orders = orderRepo.findAll();
    var items = itemRepo.findAll();

    var itemsByOrder = items.stream()
            .collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

    return orders.stream()
            .map(o -> {
              BigDecimal total = itemsByOrder
                      .getOrDefault(o.getId(), List.of())
                      .stream()
                      .map(oi -> oi.getProduct()
                              .getPrice()                              // BigDecimal
                              .multiply(BigDecimal.valueOf(oi.getQuantity())))
                      .reduce(BigDecimal.ZERO, BigDecimal::add);

              double totalAsDouble = total.doubleValue(); // response convenience

              return Map.<String, Object>of(
                      "orderId", o.getId(),
                      "createdAt", o.getCreatedAt(),
                      "total", totalAsDouble
              );
            })
            .filter(m -> ((Double) m.get("total")) >= minTotal)
            .sorted(Comparator.comparingDouble(m -> -((Double) m.get("total")))) // desc
            .collect(Collectors.toList());
  }
}
