package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_shouldReturnSavedNotification() {
        NotificationRequest request = new NotificationRequest();
        request.setOrderId(1L);
        request.setUserId(2L);
        request.setMessage("Order created successfully");

        Notification saved = new Notification();
        saved.setId(10L);
        saved.setOrderId(1L);
        saved.setUserId(2L);
        saved.setMessage("Order created successfully");
        saved.setCreatedAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponse response = notificationService.createNotification(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getOrderId()).isEqualTo(1L);
    }

    @Test
    void getNotifications_shouldFilterByUserId() {
        Notification notification = new Notification();
        notification.setId(10L);
        notification.setOrderId(1L);
        notification.setUserId(2L);
        notification.setMessage("Order created successfully");
        notification.setCreatedAt(LocalDateTime.now());

        when(notificationRepository.findByUserId(2L)).thenReturn(List.of(notification));

        List<NotificationResponse> response = notificationService.getNotifications(2L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getUserId()).isEqualTo(2L);
    }
}
