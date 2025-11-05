package com.example.orders.api;

import com.example.orders.dto.CreateProductRequest;
import com.example.orders.dto.ProductMapper;
import com.example.orders.dto.ProductResponse;
import com.example.orders.dto.UpdateProductRequest;
import com.example.orders.model.Product;
import com.example.orders.repo.ProductRepository;
import com.example.orders.repo.ProductSpecifications;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  // GET /api/products?category=&brand=&minPrice=&maxPrice=&q=&page=&size=&sort=
  @GetMapping
  public Page<ProductResponse> listProducts(
          @RequestParam(value = "category", required = false) String category,
          @RequestParam(value = "brand", required = false) String brand,
          @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
          @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
          @RequestParam(value = "q", required = false) String q,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "12") int size,
          @RequestParam(value = "sort", required = false) String sort
  ) {
    Sort sortObj = resolveSort(sort);
    Pageable pageable = PageRequest.of(page, size, sortObj);

    Specification<Product> spec = ProductSpecifications.withFilters(
            q,
            category,
            brand,
            minPrice,
            maxPrice
    );

    return productRepository.findAll(spec, pageable)
            .map(ProductMapper::toResponse);
  }

  // GET /api/products/{id}
  @GetMapping("/{id}")
  public ProductResponse getProduct(@PathVariable("id") Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            ));
    return ProductMapper.toResponse(product);
  }

  // Admin (later protected): POST /api/products
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
    Product product = ProductMapper.fromCreateRequest(request);
    Product saved = productRepository.save(product);
    return ProductMapper.toResponse(saved);
  }

  // Admin (later protected): PUT /api/products/{id}
  @PutMapping("/{id}")
  public ProductResponse updateProduct(
          @PathVariable("id") Long id,
          @Valid @RequestBody UpdateProductRequest request
  ) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            ));
    ProductMapper.updateEntity(product, request);
    Product saved = productRepository.save(product);
    return ProductMapper.toResponse(saved);
  }

  // Admin (later protected): DELETE /api/products/{id}
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable("id") Long id) {
    if (!productRepository.existsById(id)) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND,
              "Product not found"
      );
    }
    productRepository.deleteById(id);
  }

  private Sort resolveSort(String sort) {
    if (sort == null || sort.isBlank()) {
      // Default: newest first
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    // Expected format: field,dir e.g. "price,asc"
    String[] parts = sort.split(",");
    String property = parts[0];
    Sort.Direction direction = Sort.Direction.DESC;
    if (parts.length > 1) {
      direction = "asc".equalsIgnoreCase(parts[1])
              ? Sort.Direction.ASC
              : Sort.Direction.DESC;
    }
    return Sort.by(direction, property);
  }
}
