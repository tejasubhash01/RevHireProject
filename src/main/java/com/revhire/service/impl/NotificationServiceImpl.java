package com.revhire.service.impl;

import com.revhire.dto.notifications.NotificationResponse;
import com.revhire.entity.Notification;
import com.revhire.entity.User;
import com.revhire.entity.enums.NotificationType;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.NotificationRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private NotificationResponse mapToDto(Notification notification) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setType(notification.getType());
        dto.setLink(notification.getLink());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    @Override
    @Transactional
    public void createNotification(User user, String message, String type, String link) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(NotificationType.valueOf(type))
                .link(link)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(UserDetails currentUser) {
        User user = getUser(currentUser);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(UserDetails currentUser, Long notificationId) {
        User user = getUser(currentUser);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Notification not found");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(UserDetails currentUser) {
        User user = getUser(currentUser);
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalse(user.getId());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}