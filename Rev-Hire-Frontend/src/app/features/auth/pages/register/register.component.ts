import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, RegisterRequest } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  userData: RegisterRequest = {
    email: '',
    password: '',
    name: '',
    phone: '',
    location: '',
    role: 'JOB_SEEKER',
    employmentStatus: '',
    companyName: '',
    industry: '',
    companySize: '',
    companyDescription: '',
    website: '',
    securityQuestion: '',
    securityAnswer: ''
  };
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onRoleChange() {
    if (this.userData.role === 'JOB_SEEKER') {
      this.userData.companyName = '';
      this.userData.industry = '';
      this.userData.companySize = '';
      this.userData.companyDescription = '';
      this.userData.website = '';
    } else {
      this.userData.employmentStatus = '';
    }
  }

  onSubmit() {
    const dataToSend = { ...this.userData };

    if (dataToSend.employmentStatus === '') {
      delete dataToSend.employmentStatus;
    }


    console.log('Form submitted', dataToSend);

    this.authService.register(dataToSend).subscribe({
      next: (response) => {
        this.successMessage = 'Registration successful! Redirecting...';
        setTimeout(() => {
          if (response.role === 'JOB_SEEKER') {
            this.router.navigate(['/jobseeker/dashboard']);
          } else {
            this.router.navigate(['/employer/dashboard']);
          }
        }, 2000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration failed.';
        console.error(err);
      }
    });
  }
}

