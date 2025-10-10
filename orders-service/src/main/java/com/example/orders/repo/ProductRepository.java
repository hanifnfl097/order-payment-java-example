
package com.example.orders.repo;
import com.example.orders.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> { }
