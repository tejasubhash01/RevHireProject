import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployerService, EmployerProfile, UpdateEmployerProfileRequest } from '../../../../core/services/employer.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: EmployerProfile | null = null;
  loading = true;
  successMessage = '';
  errorMessage = '';

  constructor(
  private employerService: EmployerService,
  private router: Router
) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.employerService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to load profile.';
        this.loading = false;
      }
    });
  }

  onSubmit() {
    if (!this.profile) return;
    this.successMessage = '';
    this.errorMessage = '';
    const updateData: UpdateEmployerProfileRequest = {
      companyName: this.profile.companyName,
      industry: this.profile.industry,
      companySize: this.profile.companySize,
      companyDescription: this.profile.companyDescription,
      website: this.profile.website,
      location: this.profile.location
    };
    this.employerService.updateProfile(updateData).subscribe({
    next: (updated) => {
  this.profile = updated;
  this.successMessage = 'Profile updated successfully.';
  this.errorMessage = '';

  setTimeout(() => {
    this.router.navigate(['/employer/dashboard']);
  }, 1500);
},
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Update failed. Please try again.';
      }
    });
  }

  goBack() {
  this.router.navigate(['/employer/dashboard']);
}
}
