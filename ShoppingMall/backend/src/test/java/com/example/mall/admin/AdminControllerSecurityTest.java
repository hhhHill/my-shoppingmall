package com.example.mall.admin;

import com.example.mall.order.OrderRepository;
import com.example.mall.order.OrderService;
import com.example.mall.order.OrderItemRepository;
import com.example.mall.product.ProductService;
import com.example.mall.security.CustomUserDetailsService;
import com.example.mall.security.JwtAuthenticationFilter;
import com.example.mall.security.SecurityConfig;
import com.example.mall.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class AdminControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private ProductService productService;
    @MockBean private UserRepository userRepository;
    @MockBean private OrderRepository orderRepository;
    @MockBean private OrderService orderService;
    @MockBean private OrderItemRepository orderItemRepository;
    // Security beans
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = {"USER"})
    void user_forbidden_on_admin() throws Exception {
        mockMvc.perform(get("/api/admin/orders")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void admin_can_access_orders() throws Exception {
        when(orderRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
        mockMvc.perform(get("/api/admin/orders")).andExpect(status().isOk());
    }
}

