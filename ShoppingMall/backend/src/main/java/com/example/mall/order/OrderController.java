package com.example.mall.order;

import com.example.mall.order.dto.OrderItemResponse;
import com.example.mall.order.dto.OrderResponse;
import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import com.example.mall.web.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> myOrders(@AuthenticationPrincipal UserDetails principal,
                                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Page<Order> p = orderRepository.findByUserId(user.getId(), PageRequest.of(page, size));
        List<OrderResponse> items = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
        PageResponse<OrderResponse> body = new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Order order = orderRepository.findById(id).orElseThrow();
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.fail(403, "无权限"));
        }
        return ResponseEntity.ok(ApiResponse.ok(toDto(order)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Order order = orderService.createOrderFromCart(user);
        return ResponseEntity.ok(ApiResponse.ok(toDto(order)));
    }

    // Payment: POST /api/orders/{id}/pay
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<OrderResponse>> pay(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Order order = orderService.payOrder(user, id);
        return ResponseEntity.ok(ApiResponse.ok(toDto(order)));
    }

    private OrderResponse toDto(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus());
        r.setTotalAmount(order.getTotalAmount());
        r.setCreatedAt(order.getCreatedAt());
        List<OrderItemResponse> items = order.getItems().stream().map(oi -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setProductId(oi.getProduct().getId());
            ir.setProductName(oi.getProduct().getName());
            ir.setPrice(oi.getPrice());
            ir.setQuantity(oi.getQuantity());
            return ir;
        }).collect(Collectors.toList());
        r.setItems(items);
        return r;
    }
}
