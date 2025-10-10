
package com.example.orders.dto;
import jakarta.validation.constraints.*; import java.util.List;
public record CreateOrderRequest(@NotNull Long userId, @NotEmpty List<Item> items){
  public record Item(@NotNull Long productId, @Positive int quantity) { }
}
