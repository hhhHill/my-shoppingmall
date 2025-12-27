package com.example.mall.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String q);
    Page<Product> findByNameContainingIgnoreCase(String q, Pageable pageable);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByCategoryAndNameContainingIgnoreCase(String category, String q, Pageable pageable);
}
