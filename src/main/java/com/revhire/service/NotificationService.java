package com.revhire.service;

import com.revhire.dto.notifications.NotificationResponse;
import com.revhire.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface NotificationService {
    void createNotification(User user, String message, String type, String link);
    List<NotificationResponse> getMyNotifications(UserDetails currentUser);
    void markAsRead(UserDetails currentUser, Long notificationId);
    void markAllAsRead(UserDetails currentUser);
}