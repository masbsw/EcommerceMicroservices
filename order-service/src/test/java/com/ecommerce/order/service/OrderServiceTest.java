package com.ecommerce.order.service;

import com.ecommerce.order.client.NotificationClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.exception.OrderCreationException;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSaveOrderAndCallNotification() {
        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setProductId(2L);
        request.setQuantity(2);

        ProductResponse product = new ProductResponse();
        product.setId(2L);
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(10);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(1L);
        savedOrder.setProductId(2L);
        savedOrder.setQuantity(2);
        savedOrder.setTotalPrice(new BigDecimal("100.00"));

        when(productClient.getProductById(2L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(notificationClient).createOrderNotification(1L, 1L);

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("100.00");
        verify(notificationClient).createOrderNotification(1L, 1L);
    }

    @Test
    void createOrder_shouldThrowWhenStockIsNotEnough() {
        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setProductId(2L);
        request.setQuantity(20);

        ProductResponse product = new ProductResponse();
        product.setId(2L);
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(10);

        when(productClient.getProductById(2L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderCreationException.class)
                .hasMessage("Not enough stock for product id: 2");
    }
}
