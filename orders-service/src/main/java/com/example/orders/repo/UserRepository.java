
package com.example.orders.repo;
import com.example.orders.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> { }
