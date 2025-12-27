package com.example.mall.product;

import com.example.mall.log.BrowseLog;
import com.example.mall.log.BrowseLogRepository;
import com.example.mall.product.dto.ProductRequest;
import com.example.mall.product.dto.ProductResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController {
    private final ProductService productService;
    private final BrowseLogRepository browseLogRepository;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, BrowseLogRepository browseLogRepository, UserRepository userRepository) {
        this.productService = productService;
        this.browseLogRepository = browseLogRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) String category
    ) {
        String key = (keyword != null && !keyword.isBlank()) ? keyword : q;
        Page<Product> p = productService.pageProducts(key, category, PageRequest.of(page, size));
        List<ProductResponse> items = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
        PageResponse<ProductResponse> body = new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> detail(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails principal) {
        Product product = productService.getById(id);
        // browse log
        BrowseLog log = new BrowseLog();
        log.setProduct(product);
        if (principal != null) {
            User u = userRepository.findByUsername(principal.getUsername()).orElse(null);
            if (u != null) log.setUser(u);
        }
        browseLogRepository.save(log);
        return ResponseEntity.ok(ApiResponse.ok(toDto(product)));
    }

    // 管理员 CRUD（保持不变）
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(productService.create(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(productService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    private ProductResponse toDto(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setStock(p.getStock());
        r.setImageUrl(resolveImageUrl(p.getImageUrl()));
        r.setCategory(p.getCategory());
        return r;
    }

    private String resolveImageUrl(String raw) {
        if (raw == null) return null;
        String val = raw.trim();
        if (val.isEmpty()) return null;

        String lower = val.toLowerCase();
        boolean isAbsolute = lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("//");
        if (isAbsolute) {
            try {
                java.net.URI uri = java.net.URI.create(val);
                String host = uri.getHost();
                // If pointing to localhost/127.* in DB, replace with current host
                if (host != null && ("localhost".equalsIgnoreCase(host) || host.startsWith("127."))) {
                    String path = uri.getRawPath();
                    if (path == null || path.isBlank()) return val;
                    String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    if (path.startsWith("/")) return base + path;
                    return base + "/" + path;
                }
            } catch (Exception ignore) { /* fallthrough to return as-is */ }
            return val;
        }
        String path = val.startsWith("/") ? val : "/" + val;
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).toUriString();
    }
}
