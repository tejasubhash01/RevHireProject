import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password-email',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  template: `
    <div class="forgot-container">
      <div class="forgot-card">
        <h2>Forgot Password</h2>
        <p *ngIf="message"
         style="color:green;
                text-align:center;
                margin-bottom:1rem;
                font-weight:500;">
        {{ message }}
      </p>
        <p class="instruction">Enter your registered email address. We'll send you a security question.</p>
        <form (ngSubmit)="onSubmit()" #emailForm="ngForm">
          <div class="form-group">
            <label for="email">Email Address</label>
            <input
              type="email"
              id="email"
              name="email"
              [(ngModel)]="email"
              required
              email
              placeholder="Enter your email">
          </div>
          <button type="submit" [disabled]="emailForm.invalid">Continue</button>
          <p *ngIf="error" class="error-message">{{ error }}</p>
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
      margin-bottom: 0.5rem;
      color: #333;
    }
    .instruction {
      text-align: center;
      color: #666;
      margin-bottom: 1.5rem;
      font-size: 0.9rem;
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
export class ForgotPasswordEmailComponent {
  email = '';
  error = '';
  message = '';
  constructor(private authService: AuthService, private router: Router,private route: ActivatedRoute ) {
    this.route.queryParams.subscribe(params => {
      if (params['message']) {
        this.message = params['message'];
      }
    });
  }

  onSubmit() {
    this.authService.forgotPassword(this.email).subscribe({
      next: (res) => {
        this.router.navigate(['/forgot-password/question'], { state: { email: this.email } });
      },
      error: (err) => {
        this.error = err.error?.message || 'Email not found.';
      }
    });
  }
}
