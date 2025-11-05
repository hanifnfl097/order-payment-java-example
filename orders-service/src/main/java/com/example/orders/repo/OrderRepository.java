package com.example.orders.repo;

import com.example.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * Simple order history per user (untuk contoh endpoint /api/users/{id}/orders/history).
   */
  @Query(
          value = """
                    SELECT o.id          AS order_id,
                           o.created_at AS created_at,
                           o.total_amount AS total_amount
                    FROM orders o
                    WHERE o.user_id = :userId
                    ORDER BY o.created_at DESC
                    """,
          nativeQuery = true
  )
  List<Object[]> orderHistory(@Param("userId") Long userId);

  /**
   * Digunakan untuk GET /api/users/{id}/orders (list Order entity).
   */
  List<Order> findByUserId(Long userId);
}
