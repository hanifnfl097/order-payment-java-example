package com.example.orders.service;

import com.example.orders.dto.CheckoutRequest;
import com.example.orders.enums.OrderStatus;
import com.example.orders.model.Order;
import com.example.orders.model.OrderItem;
import com.example.orders.model.Product;
import com.example.orders.model.User;
import com.example.orders.repo.OrderItemRepository;
import com.example.orders.repo.OrderRepository;
import com.example.orders.repo.ProductRepository;
import com.example.orders.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CheckoutService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestClient paymentsClient;

    public CheckoutService(UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           @Value("${payments.base-url}") String paymentsBaseUrl) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;

        this.paymentsClient = RestClient.builder()
                .baseUrl(paymentsBaseUrl)
                .build();
    }

    @Transactional
    public Order checkout(CheckoutRequest request) {
        // 1) Validasi user
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User not found: " + request.userId()
                ));

        // 2) Buat order PENDING
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.shippingAddress());
        order.setContactPhone(request.contactPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(OffsetDateTime.now());
        order.setUpdatedAt(order.getCreatedAt());
        order = orderRepository.save(order); // supaya dapat ID

        BigDecimal total = BigDecimal.ZERO;

        // 3) Proses setiap item
        for (CheckoutRequest.Item itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Product not found: " + itemReq.productId()
                    ));

            // Asumsi Product punya field stockQuantity seperti di Step 2
            Integer stock = product.getStockQuantity();
            if (stock == null) {
                stock = 0;
            }

            if (stock < itemReq.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Not enough stock for product: " + product.getName()
                );
            }

            // kurangi stok
            product.setStockQuantity(stock - itemReq.quantity());
            productRepository.save(product);

            // simpan OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.quantity());
            order.getItems().add(orderItem);
            orderItemRepository.save(orderItem);

            // hitung total
            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));
            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);

        // 4) Call payments-service untuk buat payment record (fake gateway)
        PaymentCreateRequest paymentRequest = new PaymentCreateRequest(
                order.getId(),
                total.doubleValue(),   // Payment service pakai Double untuk amount
                "PAID"                 // langsung anggap berhasil
        );

        PaymentResponse paymentResponse = paymentsClient.post()
                .uri("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);

        if (paymentResponse != null) {
            order.setPaymentId(paymentResponse.id());
            if ("PAID".equalsIgnoreCase(paymentResponse.status())) {
                order.setStatus(OrderStatus.PAID);
            }
        }

        order.setUpdatedAt(OffsetDateTime.now());

        // 5) Simpan update terakhir dan kembalikan
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> findOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // ======= DTO lokal untuk call payments-service =======

    private record PaymentCreateRequest(Long orderId, Double amount, String status) { }

    private record PaymentResponse(
            Long id,
            Long orderId,
            Double amount,
            String status,
            String createdAt
    ) { }
}
