import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { JobseekerService, JobSeekerProfile } from '../../../../core/services/jobseeker.service';

@Component({
  selector: 'app-view-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './view-profile.component.html',
  styleUrls: ['./view-profile.component.css']
})
export class ViewProfileComponent implements OnInit {
  profile: JobSeekerProfile | null = null;
  loading = true;
  error = '';

  constructor(private jobseekerService: JobseekerService) {}

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
        this.error = 'Failed to load profile.';
        this.loading = false;
      }
    });
  }
}
