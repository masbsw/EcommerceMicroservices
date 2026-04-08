package com.ecommerce.order.service;

import com.ecommerce.order.client.NotificationClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.OrderCreationException;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final NotificationClient notificationClient;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        NotificationClient notificationClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.notificationClient = notificationClient;
    }

    public OrderResponse createOrder(OrderRequest request) {
        ProductResponse product = productClient.getProductById(request.getProductId());

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new OrderCreationException("Not enough stock for product id: " + request.getProductId());
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        Order savedOrder = orderRepository.save(order);
        log.info("Created order with id={} for user id={}", savedOrder.getId(), savedOrder.getUserId());
        notificationClient.createOrderNotification(savedOrder.getId(), savedOrder.getUserId());
        return toResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user id={}", userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        return response;
    }
}
