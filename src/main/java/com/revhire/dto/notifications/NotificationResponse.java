package com.revhire.dto.notifications;

import com.revhire.entity.enums.NotificationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String message;
    private Boolean isRead;
    private NotificationType type;
    private String link;
    private LocalDateTime createdAt;
}