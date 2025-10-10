
package com.example.orders.service;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.model.*; import com.example.orders.repo.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class OrderService {
  private final UserRepository userRepo; private final ProductRepository productRepo; private final OrderRepository orderRepo; private final OrderItemRepository itemRepo;
  public OrderService(UserRepository u, ProductRepository p, OrderRepository o, OrderItemRepository i){ this.userRepo=u; this.productRepo=p; this.orderRepo=o; this.itemRepo=i; }
  @Transactional public Order create(CreateOrderRequest req){
    User user = userRepo.findById(req.userId()).orElseThrow();
    Order order = new Order(); order.setUser(user); order = orderRepo.save(order);
    for (var it : req.items()){
      Product p = productRepo.findById(it.productId()).orElseThrow();
      OrderItem oi = new OrderItem(); oi.setOrder(order); oi.setProduct(p); oi.setQuantity(it.quantity()); itemRepo.save(oi);
    }
    return order;
  }
}
