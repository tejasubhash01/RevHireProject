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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LogManager.getLogger(NotificationServiceImpl.class);

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private User getUser(UserDetails userDetails) {
        log.debug("Fetching user with email: {}", userDetails.getUsername());

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new NotFoundException("User not found");
                });
    }

    private NotificationResponse mapToDto(Notification notification) {
        log.debug("Mapping Notification entity to DTO. Notification ID: {}", notification.getId());

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
        log.info("Creating notification for userId={}, type={}", user.getId(), type);

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(NotificationType.valueOf(type))
                .link(link)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        log.info("Notification created successfully for userId={}", user.getId());
    }

    @Override
    public List<NotificationResponse> getMyNotifications(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.info("Fetching notifications for userId={}", user.getId());

        List<NotificationResponse> responses =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList());

        log.info("Fetched {} notifications for userId={}", responses.size(), user.getId());
        return responses;
    }

    @Override
    @Transactional
    public void markAsRead(UserDetails currentUser, Long notificationId) {
        User user = getUser(currentUser);
        log.info("Marking notification as read. notificationId={}, userId={}",
                notificationId, user.getId());

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found. notificationId={}", notificationId);
                    return new NotFoundException("Notification not found");
                });

        if (!notification.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access attempt. notificationId={}, userId={}",
                    notificationId, user.getId());
            throw new NotFoundException("Notification not found");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);

        log.info("Notification marked as read. notificationId={}", notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UserDetails currentUser) {
        User user = getUser(currentUser);
        log.info("Marking all notifications as read for userId={}", user.getId());

        List<Notification> unread =
                notificationRepository.findByUserIdAndIsReadFalse(user.getId());

        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);

        log.info("Marked {} notifications as read for userId={}",
                unread.size(), user.getId());
    }
}