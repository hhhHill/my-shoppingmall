package com.example.mall.product;

import com.example.mall.product.dto.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listProducts(String q) {
        if (q == null || q.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(q);
    }

    public Page<Product> pageProducts(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(q, pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
    }

    public Product create(ProductRequest req) {
        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setImageUrl(req.getImageUrl());
        p.setCategory(req.getCategory());
        return productRepository.save(p);
    }

    public Product update(Long id, ProductRequest req) {
        Product p = getById(id);
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setImageUrl(req.getImageUrl());
        p.setCategory(req.getCategory());
        return productRepository.save(p);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
