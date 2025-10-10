
package com.example.orders.api;
import com.example.orders.dto.CreateProductRequest; import com.example.orders.model.Product; import com.example.orders.repo.ProductRepository;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/products")
public class ProductController {
  private final ProductRepository repo; public ProductController(ProductRepository r){ this.repo=r; }
  @GetMapping public List<Product> all(){ return repo.findAll(); }
  @PostMapping public Product create(@Valid @RequestBody CreateProductRequest req){ Product p=new Product(); p.setName(req.name()); p.setPrice(req.price()); return repo.save(p); }
}
