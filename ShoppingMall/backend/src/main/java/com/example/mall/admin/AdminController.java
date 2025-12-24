package com.example.mall.admin;

import com.example.mall.product.Product;
import com.example.mall.product.ProductService;
import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ProductService productService;
    private final UserRepository userRepository;

    public AdminController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> products() {
        return ResponseEntity.ok(ApiResponse.ok(productService.listProducts(null)));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> users() {
        return ResponseEntity.ok(ApiResponse.ok(userRepository.findAll()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> stats() {
        // 简化占位：返回零值统计
        return ResponseEntity.ok(ApiResponse.ok(new Stats(0L, 0L, 0L)));
    }

    record Stats(Long totalUsers, Long totalOrders, Long totalProducts) {}
}

