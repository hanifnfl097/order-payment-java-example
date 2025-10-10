
package com.example.orders.dto;
import jakarta.validation.constraints.*;
public record CreateUserRequest(@NotBlank String name, @Email String email) { }
