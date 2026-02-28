import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password-reset',
  standalone: true,
  imports: [FormsModule, CommonModule,RouterLink],
  template: `
    <div class="forgot-container">
      <div class="forgot-card">
        <h2>Reset Password</h2>
        <form (ngSubmit)="onSubmit()" #resetForm="ngForm">
          <div class="form-group">
            <label for="newPassword">New Password</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              [(ngModel)]="newPassword"
              required
              placeholder="Enter new password">
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              [(ngModel)]="confirmPassword"
              required
              placeholder="Confirm new password">
          </div>
          <button type="submit" [disabled]="resetForm.invalid || newPassword !== confirmPassword">
            Reset Password
          </button>
          <p *ngIf="error" class="error-message">{{ error }}</p>
          <p *ngIf="success" class="success-message">{{ success }}</p>
        </form>
        <p class="back-link"><a routerLink="/login">← Back to Login</a></p>
      </div>
    </div>
  `,
  styles: [`
    .forgot-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      background-color: #d6eaf8;
    }
    .forgot-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      width: 100%;
      max-width: 400px;
    }
    h2 {
      text-align: center;
      margin-bottom: 1.5rem;
      color: #333;
    }
    .form-group {
      margin-bottom: 1.5rem;
    }
    label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: #555;
    }
    input {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }
    input:focus {
      outline: none;
      border-color: #007bff;
    }
    button {
      width: 100%;
      padding: 0.75rem;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      transition: background-color 0.2s;
    }
    button:hover:not(:disabled) {
      background-color: #0056b3;
    }
    button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }
    .error-message {
      color: #dc3545;
      text-align: center;
      margin-top: 1rem;
    }
    .success-message {
      color: #28a745;
      text-align: center;
      margin-top: 1rem;
    }
    .back-link {
      text-align: center;
      margin-top: 1.5rem;
    }
    .back-link a {
      color: #007bff;
      text-decoration: none;
    }
    .back-link a:hover {
      text-decoration: underline;
    }
  `]
})
export class ForgotPasswordResetComponent {
  newPassword = '';
  confirmPassword = '';
  error = '';
  success = '';
  token = '';

  constructor(private authService: AuthService, private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    this.token = navigation?.extras.state?.['token'] || '';
    if (!this.token) {
      this.router.navigate(['/forgot-password']);
    }
  }

  onSubmit() {
    if (this.newPassword !== this.confirmPassword) {
      this.error = 'Passwords do not match.';
      return;
    }
    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.success = 'Password reset successfully. Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Reset failed.';
      }
    });
  }
}

