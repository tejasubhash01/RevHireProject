import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NotificationService, NotificationResponse } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-notifications-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.css']
})
export class NotificationsListComponent implements OnInit {
  notifications: NotificationResponse[] = [];
  loading = true;
  error = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.notificationService.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load notifications.';
        this.loading = false;
      }
    });
  }

  markAsRead(id: number) {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to mark as read.');
      }
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to mark all as read.');
      }
    });
  }

  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
  }
}
