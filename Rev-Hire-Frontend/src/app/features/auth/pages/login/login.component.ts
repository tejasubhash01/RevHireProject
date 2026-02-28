import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, LoginRequest } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials: LoginRequest = { email: '', password: '' };
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        if (response.role === 'JOB_SEEKER') {
          this.router.navigate(['/jobseeker/dashboard']);
        } else if (response.role === 'EMPLOYER') {
          this.router.navigate(['/employer/dashboard']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.errorMessage = 'Login failed. Please check your credentials.';
      }
    });
  }
}

