import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  unreadCount = 0;
  private notificationSub: Subscription | null = null;

  constructor(
    public authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loadUnreadCount();
    }
  }

  loadUnreadCount() {
    this.notificationSub = this.notificationService.getMyNotifications().subscribe({
      next: (notifications) => {
        this.unreadCount = notifications.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Failed to load notifications', err)
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    this.notificationSub?.unsubscribe();
  }
}
