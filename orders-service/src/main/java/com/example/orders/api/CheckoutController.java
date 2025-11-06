package com.example.orders.api;

import com.example.orders.dto.CheckoutRequest;
import com.example.orders.dto.CheckoutResponse;
import com.example.orders.model.Order;
import com.example.orders.security.AppUserDetails;
import com.example.orders.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * Checkout:
     * - JWT wajib (user diambil dari token / AuthenticationPrincipal)
     * - userId di body diabaikan, tapi tetap ada di DTO
     */
    @PostMapping("/orders/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request,
                                     @AuthenticationPrincipal AppUserDetails currentUser) {

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        Long effectiveUserId = currentUser.getId();

        CheckoutRequest fixedRequest = new CheckoutRequest(
                effectiveUserId,
                request.items(),
                request.shippingAddress(),
                request.contactPhone()
        );

        Order order = checkoutService.checkout(fixedRequest);
        return toResponse(order);
    }

    /**
     * Ambil order milik user tertentu.
     * - USER biasa hanya boleh lihat order miliknya sendiri
     * - ADMIN boleh lihat order user mana pun
     */
    @GetMapping("/users/{userId}/orders")
    public List<CheckoutResponse> userOrders(
            @PathVariable("userId") Long userId,                 // <== PENTING: sebutkan nama "userId"
            @AuthenticationPrincipal AppUserDetails currentUser  // user dari JWT
    ) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        boolean isAdmin = "ADMIN".equals(currentUser.getRole().name());
        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view orders of another user"
            );
        }

        List<Order> orders = checkoutService.findOrdersForUser(userId);
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    // ====== mapper Order -> CheckoutResponse ======

    private CheckoutResponse toResponse(Order order) {
        var lines = order.getItems().stream()
                .map(oi -> new CheckoutResponse.Line(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity()
                ))
                .toList();

        return new CheckoutResponse(
                order.getId(),
                order.getUser().getId(),
                order.getShippingAddress(),
                order.getContactPhone(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getPaymentId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                lines
        );
    }
}
