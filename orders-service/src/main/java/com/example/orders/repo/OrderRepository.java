
package com.example.orders.repo;
import com.example.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
  @Query(value = "SELECT o.id as order_id, u.name as user_name, o.created_at as created_at, SUM(oi.quantity * p.price) as total_amount FROM orders o JOIN users u ON u.id = o.user_id JOIN order_items oi ON oi.order_id = o.id JOIN products p ON p.id = oi.product_id WHERE u.id = :userId GROUP BY o.id, u.name, o.created_at ORDER BY o.created_at DESC", nativeQuery = true)
  List<Object[]> orderHistory(@Param("userId") Long userId);
}
