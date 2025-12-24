package com.example.mall.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    interface HotProductView {
        Long getProductId();
        String getProductName();
        Long getQuantity();
    }

    @Query(value = "SELECT oi.product_id AS productId, p.name AS productName, SUM(oi.quantity) AS quantity " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON p.id = oi.product_id " +
            "WHERE o.status IN ('PAID','COMPLETED') AND o.created_at >= :from AND o.created_at < :to " +
            "GROUP BY oi.product_id, p.name " +
            "ORDER BY quantity DESC " +
            "LIMIT 5", nativeQuery = true)
    List<HotProductView> findTopHotProducts(@Param("from") Instant from, @Param("to") Instant to);
}

