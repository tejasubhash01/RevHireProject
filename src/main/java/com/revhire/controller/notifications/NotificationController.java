package com.revhire.controller.notifications;
import com.revhire.dto.ApiResponse;
import com.revhire.dto.notifications.NotificationResponse;
import com.revhire.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ApiResponse<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal UserDetails currentUser) {
        List<NotificationResponse> notifications = notificationService.getMyNotifications(currentUser);
        return ApiResponse.success("Notifications retrieved", notifications);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long id) {
        notificationService.markAsRead(currentUser, id);
        return ApiResponse.success("Notification marked as read", null);
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@AuthenticationPrincipal UserDetails currentUser) {
        notificationService.markAllAsRead(currentUser);
        return ApiResponse.success("All notifications marked as read", null);
    }
}