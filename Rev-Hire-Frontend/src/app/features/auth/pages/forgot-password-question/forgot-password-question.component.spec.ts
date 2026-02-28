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
      <div class="card">
        <h2>Security Question</h2>
        <p>{{ question }}</p>
        <form (ngSubmit)="onSubmit()" #answerForm="ngForm">
          <div class="form-group">
            <label for="answer">Answer</label>
            <input type="text" id="answer" name="answer" [(ngModel)]="answer" required>
          </div>
          <button type="submit" [disabled]="answerForm.invalid">Verify</button>
          <p *ngIf="error" class="error">{{ error }}</p>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .forgot-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; }
    .card { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); width: 100%; max-width: 400px; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.5rem; font-weight: 500; }
    input { width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px; }
    button { width: 100%; padding: 0.75rem; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    button:disabled { background-color: #ccc; }
    .error { color: #dc3545; margin-top: 0.5rem; }
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
      // Fetch question (already done in previous step, but we need to display it)
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
}
