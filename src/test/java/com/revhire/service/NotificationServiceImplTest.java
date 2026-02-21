package com.revhire.service;

import com.revhire.dto.notifications.NotificationResponse;
import com.revhire.entity.Notification;
import com.revhire.entity.User;
import com.revhire.entity.enums.NotificationType;
import com.revhire.entity.enums.Role;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.NotificationRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UserDetails mockUserDetails;
    private User mockUser;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@test.com")
                .password("pass")
                .roles("USER")
                .build();

        mockUser = User.builder()
                .email("user@test.com")
                .name("Test User")
                .role(Role.JOB_SEEKER)
                .build();
        mockUser.setId(1L);

        mockNotification = Notification.builder()
                .user(mockUser)
                .message("Test notification")
                .type(NotificationType.APPLICATION_UPDATE)
                .isRead(false)
                .link("/test")
                .build();
        mockNotification.setId(100L);
        mockNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createNotification_SavesNotification() {
        notificationService.createNotification(mockUser, "Message", "APPLICATION_UPDATE", "/link");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getMyNotifications_ReturnsList() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(mockUser.getId()))
                .thenReturn(List.of(mockNotification));

        List<NotificationResponse> result = notificationService.getMyNotifications(mockUserDetails);

        assertEquals(1, result.size());
        assertEquals(mockNotification.getMessage(), result.get(0).getMessage());
    }

    @Test
    void getMyNotifications_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.getMyNotifications(mockUserDetails));
    }

    @Test
    void markAsRead_Success() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        notificationService.markAsRead(mockUserDetails, 100L);

        assertTrue(mockNotification.getIsRead());
        verify(notificationRepository, times(1)).save(mockNotification);
    }

    @Test
    void markAsRead_NotificationNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.markAsRead(mockUserDetails, 999L));
    }

    @Test
    void markAsRead_NotificationNotOwned_ThrowsNotFoundException() {
        User otherUser = User.builder().email("other@test.com").build();
        otherUser.setId(999L);
        mockNotification.setUser(otherUser);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(mockNotification));

        assertThrows(NotFoundException.class, () -> notificationService.markAsRead(mockUserDetails, 100L));
    }

    @Test
    void markAllAsRead_Success() {
        Notification unread1 = Notification.builder().user(mockUser).isRead(false).build();
        unread1.setId(1L);
        Notification unread2 = Notification.builder().user(mockUser).isRead(false).build();
        unread2.setId(2L);
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(notificationRepository.findByUserIdAndIsReadFalse(mockUser.getId()))
                .thenReturn(List.of(unread1, unread2));

        notificationService.markAllAsRead(mockUserDetails);

        assertTrue(unread1.getIsRead());
        assertTrue(unread2.getIsRead());
        verify(notificationRepository, times(1)).saveAll(List.of(unread1, unread2));
    }
}