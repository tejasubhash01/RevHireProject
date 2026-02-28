import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password-question',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="forgot-container">
      <div class="forgot-card">
        <h2>Security Question</h2>
        <p class="question">{{ question }}</p>
        <form (ngSubmit)="onSubmit()" #answerForm="ngForm">
          <div class="form-group">
            <label for="answer">Your Answer</label>
            <input
              type="text"
              id="answer"
              name="answer"
              [(ngModel)]="answer"
              required
              placeholder="Enter your answer">
          </div>
          <button type="submit" [disabled]="answerForm.invalid">Verify Answer</button>
          <p *ngIf="error" class="error-message">{{ error }}</p>
        </form>
        <p style="text-align:center; margin-top:1.5rem;">
  <button
    type="button"
    (click)="goBack()"
    style="
      background:none;
      border:none;
      color:#007bff;
      text-decoration:underline;
      cursor:pointer;
      font-size:1rem;
    ">
    ← Back to Email
  </button>
</p>
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
      margin-bottom: 1rem;
      color: #333;
    }
    .question {
      font-size: 1.1rem;
      font-weight: 500;
      color: #007bff;
      text-align: center;
      padding: 1rem;
      background: #f0f8ff;
      border-radius: 4px;
      margin-bottom: 1.5rem;
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
export class ForgotPasswordQuestionComponent {
  question = '';
  answer = '';
  error = '';
  email = '';

  constructor(private authService: AuthService, private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    this.email = navigation?.extras.state?.['email'] || '';
    if (!this.email) {
      this.router.navigate(['/forgot-password']);
    } else {
      this.authService.forgotPassword(this.email).subscribe({
        next: (res) => this.question = res.question,
        error: () => this.router.navigate(['/forgot-password'])
      });
    }
  }

  onSubmit() {
    this.authService.verifyAnswer(this.email, this.answer).subscribe({
      next: (res) => {
        this.router.navigate(['/forgot-password/reset'], { state: { token: res.token } });
      },
      error: (err) => {
        this.error = err.error?.message || 'Incorrect answer.';
      }
    });
  }
  goBack() {
  this.router.navigate(['/forgot-password'], {
  });
}
}
