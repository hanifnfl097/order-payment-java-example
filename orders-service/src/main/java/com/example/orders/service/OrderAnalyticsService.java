
package com.example.orders.service;
import com.example.orders.repo.OrderItemRepository; import com.example.orders.repo.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.*; import java.util.stream.Collectors;
@Service public class OrderAnalyticsService {
  private final OrderRepository orderRepo; private final OrderItemRepository itemRepo;
  public OrderAnalyticsService(OrderRepository o, OrderItemRepository i){ this.orderRepo=o; this.itemRepo=i; }
  public List<Map<String,Object>> findLargeOrders(double minTotal){
    var orders = orderRepo.findAll();
    var items = itemRepo.findAll();
    var itemsByOrder = items.stream().collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));
    return orders.stream().map(o -> {
      double total = itemsByOrder.getOrDefault(o.getId(), List.of()).stream().mapToDouble(oi -> oi.getProduct().getPrice() * oi.getQuantity()).sum();
      return Map.<String,Object>of("orderId", o.getId(), "createdAt", o.getCreatedAt(), "total", total);
    }).filter(m -> ((Double)m.get("total")) >= minTotal).sorted(Comparator.comparing(m -> (Double)m.get("total"), Comparator.reverseOrder())).collect(Collectors.toList());
  }
}
