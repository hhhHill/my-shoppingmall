package com.example.mall.mail;

import com.example.mall.order.Order;
import com.example.mall.order.OrderItem;
import com.example.mall.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ApplicationContext applicationContext;
    private final String mode; // console | smtp
    private final String from;

    public MailService(ApplicationContext applicationContext,
                       @Value("${app.mail.mode:console}") String mode,
                       @Value("${spring.mail.username:no-reply@example.com}") String from) {
        this.applicationContext = applicationContext;
        this.mode = mode;
        this.from = from;
    }

    public void sendPaymentSuccess(User user, Order order) {
        String subject = "Payment success for order #" + order.getId();
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getUsername()).append(", your payment succeeded.\n");
        body.append("Order ID: ").append(order.getId()).append("\n");
        body.append("Items:\n");
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem oi : order.getItems()) {
            BigDecimal sub = oi.getPrice().multiply(new BigDecimal(oi.getQuantity()));
            total = total.add(sub);
            body.append(" - ").append(oi.getProduct().getName())
                .append(" x").append(oi.getQuantity())
                .append(" @ ").append(oi.getPrice())
                .append(" = ").append(sub).append("\n");
        }
        body.append("Total: ").append(total);

        if ("smtp".equalsIgnoreCase(mode) && user.getEmail() != null && !user.getEmail().isBlank()) {
            try {
                // Try to load JavaMailSender and SimpleMailMessage via reflection to avoid hard dependency
                Class<?> senderClass = Class.forName("org.springframework.mail.javamail.JavaMailSender");
                Object senderBean = applicationContext.getBean(senderClass);
                Class<?> msgClass = Class.forName("org.springframework.mail.SimpleMailMessage");
                Object msg = msgClass.getDeclaredConstructor().newInstance();
                Method setFrom = msgClass.getMethod("setFrom", String.class);
                Method setTo = msgClass.getMethod("setTo", String[].class);
                Method setSubject = msgClass.getMethod("setSubject", String.class);
                Method setText = msgClass.getMethod("setText", String.class);
                setFrom.invoke(msg, from);
                setTo.invoke(msg, (Object) new String[]{user.getEmail()});
                setSubject.invoke(msg, subject);
                setText.invoke(msg, body.toString());
                Method send = senderClass.getMethod("send", msgClass);
                send.invoke(senderBean, msg);
                log.info("Sent payment email to {} for order {}", user.getEmail(), order.getId());
                return;
            } catch (Throwable e) {
                log.warn("SMTP send failed or mail classes missing, fallback to console: {}", e.getMessage());
            }
        }
        // console fallback
        System.out.println("[MAIL][CONSOLE] to=" + (user.getEmail() == null ? "<no-email>" : user.getEmail()) +
                ", subject=" + subject + "\n" + body);
    }
}
