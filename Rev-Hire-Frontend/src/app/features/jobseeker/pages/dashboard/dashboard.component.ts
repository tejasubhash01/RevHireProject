import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { JobseekerService, JobSeekerProfile } from '../../../../core/services/jobseeker.service';
import { ApplicationService } from '../../../../core/services/application.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  profile: JobSeekerProfile | null = null;
  loading = true;
  error = '';
  stats = {
    total: 0,
    applied: 0,
    underReview: 0,
    shortlisted: 0,
    rejected: 0,
    withdrawn: 0
  };

  constructor(
    private jobseekerService: JobseekerService,
    private applicationService: ApplicationService
  ) {}

  ngOnInit() {
    this.loadProfile();
    this.loadApplicationStats();
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

  loadApplicationStats() {
    this.applicationService.getMyApplications().subscribe({
      next: (applications) => {
        this.stats.total = applications.length;
        this.stats.applied = applications.filter(a => a.status === 'APPLIED').length;
        this.stats.underReview = applications.filter(a => a.status === 'UNDER_REVIEW').length;
        this.stats.shortlisted = applications.filter(a => a.status === 'SHORTLISTED').length;
        this.stats.rejected = applications.filter(a => a.status === 'REJECTED').length;
        this.stats.withdrawn = applications.filter(a => a.status === 'WITHDRAWN').length;
      },
      error: (err) => console.error(err)
    });
  }
}
