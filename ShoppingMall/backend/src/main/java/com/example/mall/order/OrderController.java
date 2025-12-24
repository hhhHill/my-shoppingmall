package com.example.mall.order;

import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> myOrders(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(orderRepository.findByUserId(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createOrder() {
        // 简化：此处仅返回占位结果，真实逻辑可根据购物车生成订单
        return ResponseEntity.ok(ApiResponse.ok("下单成功(示例)", null));
    }
}

