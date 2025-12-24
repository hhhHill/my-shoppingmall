package com.example.mall.admin;

import com.example.mall.order.*;
import com.example.mall.order.dto.OrderResponse;
import com.example.mall.product.Product;
import com.example.mall.product.ProductService;
import com.example.mall.product.dto.ProductRequest;
import com.example.mall.product.dto.ProductResponse;
import com.example.mall.user.Role;
import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import com.example.mall.web.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public AdminController(ProductService productService,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderService orderService,
                           OrderItemRepository orderItemRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    // 商品管理
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest req) {
        Product p = productService.create(req);
        return ResponseEntity.ok(ApiResponse.ok(toDto(p)));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
                                                                      @Valid @RequestBody ProductRequest req) {
        Product p = productService.update(id, req);
        return ResponseEntity.ok(ApiResponse.ok(toDto(p)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    // 订单管理
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> listOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        Page<Order> result;
        if (status != null && !status.isBlank() && userId != null) {
            OrderStatus st = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
            result = orderRepository.findByUserIdAndStatus(userId, st, PageRequest.of(page, size));
        } else if (status != null && !status.isBlank()) {
            OrderStatus st = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
            result = orderRepository.findByStatus(st, PageRequest.of(page, size));
        } else if (userId != null) {
            result = orderRepository.findByUserId(userId, PageRequest.of(page, size));
        } else {
            result = orderRepository.findAll(PageRequest.of(page, size));
        }
        List<OrderResponse> items = result.getContent().stream().map(this::toOrderDto).collect(Collectors.toList());
        PageResponse<OrderResponse> body = new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    public record UpdateOrderStatusRequest(String status) {}

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long id,
                                                                        @RequestBody UpdateOrderStatusRequest req) {
        Set<String> allowed = Set.of("SHIPPED", "COMPLETED", "CANCELLED");
        String s = req.status() == null ? "" : req.status().toUpperCase(Locale.ROOT);
        if (!allowed.contains(s)) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "非法状态: " + req.status()));
        }
        Order order = orderService.adminUpdateStatus(id, OrderStatus.valueOf(s));
        return ResponseEntity.ok(ApiResponse.ok(toOrderDto(order)));
    }

    // 用户管理
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> users(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Page<User> p;
        if (keyword != null && !keyword.isBlank()) {
            p = userRepository.searchByUsernameOrEmail(keyword, PageRequest.of(page, size));
        } else {
            p = userRepository.findAll(PageRequest.of(page, size));
        }
        List<UserResponse> items = p.getContent().stream().map(this::toUserDto).collect(Collectors.toList());
        PageResponse<UserResponse> body = new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    public record UpdateUserRoleRequest(String role) {}

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(@PathVariable Long id,
                                                                    @RequestBody UpdateUserRoleRequest req) {
        String roleStr = req.role() == null ? "" : req.role().toUpperCase(Locale.ROOT);
        if (!roleStr.equals("USER") && !roleStr.equals("ADMIN")) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "非法角色: " + req.role()));
        }
        User u = userRepository.findById(id).orElseThrow();
        u.setRole(Role.valueOf(roleStr));
        userRepository.save(u);
        return ResponseEntity.ok(ApiResponse.ok(toUserDto(u)));
    }

    // 销售统计报表
    public record HotProduct(Long productId, String productName, Long quantity) {}
    public record SalesStats(long totalOrders, long paidOrders, BigDecimal salesAmount, List<HotProduct> topProducts) {}

    @GetMapping("/stats/sales")
    public ResponseEntity<ApiResponse<SalesStats>> salesStats(@RequestParam("from") String from,
                                                              @RequestParam("to") String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        Instant fromTs = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toTs = toDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        long total = orderRepository.countByCreatedAtBetween(fromTs, toTs);
        long paid = orderRepository.countByStatusInAndCreatedAtBetween(List.of(OrderStatus.PAID, OrderStatus.COMPLETED), fromTs, toTs);
        BigDecimal sales = orderRepository.sumTotalAmountByStatusInAndCreatedAtBetween(List.of(OrderStatus.PAID, OrderStatus.COMPLETED), fromTs, toTs);
        List<OrderItemRepository.HotProductView> hot = orderItemRepository.findTopHotProducts(fromTs, toTs);
        List<HotProduct> top = hot.stream()
                .map(h -> new HotProduct(h.getProductId(), h.getProductName(), h.getQuantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(new SalesStats(total, paid, sales, top)));
    }

    private ProductResponse toDto(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setStock(p.getStock());
        r.setImageUrl(p.getImageUrl());
        r.setCategory(p.getCategory());
        return r;
    }

    private OrderResponse toOrderDto(Order order) {
        // Reuse OrderController mapping style
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus());
        r.setTotalAmount(order.getTotalAmount());
        r.setCreatedAt(order.getCreatedAt());
        var items = order.getItems().stream().map(oi -> {
            var ir = new com.example.mall.order.dto.OrderItemResponse();
            ir.setProductId(oi.getProduct().getId());
            ir.setProductName(oi.getProduct().getName());
            ir.setPrice(oi.getPrice());
            ir.setQuantity(oi.getQuantity());
            return ir;
        }).collect(Collectors.toList());
        r.setItems(items);
        return r;
    }

    public static class UserResponse {
        public Long id;
        public String username;
        public String email;
        public String role;
        public boolean enabled;
        public Instant createdAt;
    }

    private UserResponse toUserDto(User u) {
        UserResponse r = new UserResponse();
        r.id = u.getId();
        r.username = u.getUsername();
        r.email = u.getEmail();
        r.role = u.getRole().name();
        r.enabled = u.isEnabled();
        r.createdAt = u.getCreatedAt();
        return r;
    }
}
