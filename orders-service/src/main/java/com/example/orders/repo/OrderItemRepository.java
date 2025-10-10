
package com.example.orders.repo;
import com.example.orders.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  @Query(value = "SELECT p.name, SUM(oi.quantity) as qty FROM order_items oi JOIN products p ON p.id = oi.product_id GROUP BY p.name ORDER BY qty DESC LIMIT :limit", nativeQuery = true)
  List<Object[]> topProducts(@Param("limit") int limit);
}
