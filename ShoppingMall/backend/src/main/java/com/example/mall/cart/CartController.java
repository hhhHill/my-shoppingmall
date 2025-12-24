package com.example.mall.cart;

import com.example.mall.cart.dto.CartItemRequest;
import com.example.mall.cart.dto.CartItemResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> myCart(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        List<CartItemResponse> items = cartItemRepository.findByUserId(user.getId()).stream()
                .map(ci -> toDto(ci.getProduct(), ci.getQuantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    // New spec: POST /api/cart/items -> add/update quantity
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addOrUpdate(@AuthenticationPrincipal UserDetails principal,
                                                                     @Valid @RequestBody CartItemRequest req) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Product product = productRepository.findById(req.getProductId()).orElseThrow();
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId()).orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
        }
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);
        return ResponseEntity.ok(ApiResponse.ok(toDto(product, item.getQuantity())));
    }

    // New spec: DELETE /api/cart/items/{productId}
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Object>> removeByProduct(@PathVariable Long productId,
                                                               @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    // Backward compatible endpoints used by existing frontend
    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> add(@AuthenticationPrincipal UserDetails principal,
                                                             @Valid @RequestBody CartItemRequest req) {
        return addOrUpdate(principal, req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody CartItemRequest req,
                                                                @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        CartItem item = cartItemRepository.findById(id).orElseThrow();
        if (!item.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.fail(403, "无权限"));
        }
        Product product = productRepository.findById(req.getProductId()).orElseThrow();
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);
        return ResponseEntity.ok(ApiResponse.ok(toDto(product, item.getQuantity())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> remove(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        CartItem item = cartItemRepository.findById(id).orElseThrow();
        if (!item.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.fail(403, "无权限"));
        }
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    private CartItemResponse toDto(Product product, int quantity) {
        CartItemResponse r = new CartItemResponse();
        r.setProductId(product.getId());
        r.setProductName(product.getName());
        r.setPrice(product.getPrice());
        r.setQuantity(quantity);
        r.setSubtotal(product.getPrice().multiply(new BigDecimal(quantity)));
        return r;
    }
}
