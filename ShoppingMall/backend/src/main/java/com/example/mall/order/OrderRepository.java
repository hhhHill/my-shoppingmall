package com.example.mall.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.Instant;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findById(Long aLong);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    @Query("select count(o) from Order o where o.createdAt >= :from and o.createdAt < :to")
    long countByCreatedAtBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("select count(o) from Order o where o.status in :statuses and o.createdAt >= :from and o.createdAt < :to")
    long countByStatusInAndCreatedAtBetween(@Param("statuses") List<OrderStatus> statuses,
                                            @Param("from") Instant from,
                                            @Param("to") Instant to);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status in :statuses and o.createdAt >= :from and o.createdAt < :to")
    BigDecimal sumTotalAmountByStatusInAndCreatedAtBetween(@Param("statuses") List<OrderStatus> statuses,
                                                           @Param("from") Instant from,
                                                           @Param("to") Instant to);
}
