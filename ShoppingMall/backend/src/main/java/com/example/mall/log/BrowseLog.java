package com.example.mall.log;

import com.example.mall.product.Product;
import com.example.mall.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "browse_logs")
public class BrowseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 允许为 null（未登录）

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "action", nullable = false, length = 32)
    private String action = "VIEW";

    @Column(name = "created_at", nullable = false)
    private Instant viewedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Instant getViewedAt() { return viewedAt; }
    public void setViewedAt(Instant viewedAt) { this.viewedAt = viewedAt; }
}
