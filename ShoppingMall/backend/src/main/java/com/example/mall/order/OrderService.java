package com.example.mall.order;

import com.example.mall.cart.CartItem;
import com.example.mall.cart.CartItemRepository;
import com.example.mall.exception.BusinessException;
import com.example.mall.log.PurchaseLog;
import com.example.mall.log.PurchaseLogRepository;
import com.example.mall.mail.MailService;
import com.example.mall.product.Product;
import com.example.mall.product.ProductRepository;
import com.example.mall.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final PurchaseLogRepository purchaseLogRepository;
    private final MailService mailService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CartItemRepository cartItemRepository,
                        PurchaseLogRepository purchaseLogRepository,
                        MailService mailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.purchaseLogRepository = purchaseLogRepository;
        this.mailService = mailService;
    }

    @Transactional
    public Order createOrderFromCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // check stock
        for (CartItem ci : cartItems) {
            Product p = productRepository.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
            if (p.getStock() < ci.getQuantity()) {
                throw new BusinessException("库存不足: " + p.getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = productRepository.findById(ci.getProduct().getId()).orElseThrow();
            // deduct stock
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            items.add(oi);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setItems(items);
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        // clear cart
        cartItemRepository.deleteByUserId(user.getId());

        return order;
    }

    @Transactional
    public Order payOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("无权限");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("订单状态不支持支付");
        }
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        // logs
        for (OrderItem oi : order.getItems()) {
            PurchaseLog log = new PurchaseLog();
            log.setUser(user);
            log.setOrder(order);
            log.setProduct(oi.getProduct());
            log.setQuantity(oi.getQuantity());
            purchaseLogRepository.save(log);
        }

        // email
        mailService.sendPaymentSuccess(user, order);

        return order;
    }

    @Transactional
    public Order adminUpdateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在"));
        OrderStatus current = order.getStatus();
        boolean allowed = switch (current) {
            case CREATED -> newStatus == OrderStatus.CANCELLED;
            case PAID -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException("非法状态流转: " + current + " -> " + newStatus);
        }
        order.setStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }
}
