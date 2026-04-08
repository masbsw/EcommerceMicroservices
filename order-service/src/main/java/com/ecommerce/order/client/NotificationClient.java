package com.ecommerce.order.client;

import com.ecommerce.order.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public NotificationClient(RestTemplate restTemplate,
                              @Value("${notification-service.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    public void createOrderNotification(Long orderId, Long userId) {
        NotificationRequest request = new NotificationRequest();
        request.setOrderId(orderId);
        request.setUserId(userId);
        request.setMessage("Order created successfully");

        try {
            restTemplate.postForObject(notificationServiceUrl + "/notifications", request, Void.class);
        } catch (Exception ex) {
            log.error("Failed to create notification for order id={}", orderId, ex);
        }
    }
}
