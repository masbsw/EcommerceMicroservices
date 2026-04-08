package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setOrderId(request.getOrderId());
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification for order id={}", savedNotification.getOrderId());
        return toResponse(savedNotification);
    }

    public List<NotificationResponse> getNotifications(Long userId) {
        log.info("Fetching notifications for user id={}", userId);

        List<Notification> notifications = userId == null
                ? notificationRepository.findAll()
                : notificationRepository.findByUserId(userId);

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setOrderId(notification.getOrderId());
        response.setUserId(notification.getUserId());
        response.setMessage(notification.getMessage());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
