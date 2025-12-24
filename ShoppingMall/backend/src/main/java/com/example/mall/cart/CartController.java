package com.example.mall.cart;

import com.example.mall.cart.dto.CartItemRequest;
import com.example.mall.product.Product;
import com.example.mall.product.ProductRepository;
import com.example.mall.user.User;
import com.example.mall.user.UserRepository;
import com.example.mall.web.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart")
public class CartController {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> myCart(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(cartItemRepository.findByUserId(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItem>> add(@AuthenticationPrincipal UserDetails principal,
                                                     @Valid @RequestBody CartItemRequest req) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Product product = productRepository.findById(req.getProductId()).orElseThrow();
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(req.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok(cartItemRepository.save(item)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItem>> update(@PathVariable Long id,
                                                        @Valid @RequestBody CartItemRequest req,
                                                        @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        CartItem item = cartItemRepository.findById(id).orElseThrow();
        if (!item.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.fail("无权限"));
        }
        item.setQuantity(req.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok(cartItemRepository.save(item)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> remove(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        CartItem item = cartItemRepository.findById(id).orElseThrow();
        if (!item.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.fail("无权限"));
        }
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }
}

