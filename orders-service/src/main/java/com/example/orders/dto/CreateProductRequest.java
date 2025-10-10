
package com.example.orders.dto;
import jakarta.validation.constraints.*;
public record CreateProductRequest(@NotBlank String name, @Positive double price) { }
