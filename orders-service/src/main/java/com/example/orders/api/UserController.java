
package com.example.orders.api;
import com.example.orders.dto.CreateUserRequest; import com.example.orders.model.User; import com.example.orders.repo.UserRepository;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/users")
public class UserController {
  private final UserRepository repo; public UserController(UserRepository r){ this.repo=r; }
  @GetMapping public List<User> all(){ return repo.findAll(); }
  @PostMapping public User create(@Valid @RequestBody CreateUserRequest req){ User u=new User(); u.setName(req.name()); u.setEmail(req.email()); return repo.save(u); }
}
