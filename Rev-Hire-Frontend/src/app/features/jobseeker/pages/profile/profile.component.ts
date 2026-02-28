import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { JobseekerService, JobSeekerProfile, CreateJobSeekerProfileRequest } from '../../../../core/services/jobseeker.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: JobSeekerProfile | null = null;
  loading = true;
  successMessage = '';
  errorMessage = '';

  constructor(
    private jobseekerService: JobseekerService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.jobseekerService.getProfile().subscribe({
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
    const updateData: CreateJobSeekerProfileRequest = {
      headline: this.profile.headline,
      summary: this.profile.summary,
      employmentStatus: this.profile.employmentStatus
    };
    this.jobseekerService.updateProfile(updateData).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.successMessage = 'Profile updated successfully.';
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Update failed.';
      }
    });
  }
}
