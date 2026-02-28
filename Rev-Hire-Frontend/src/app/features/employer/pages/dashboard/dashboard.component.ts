import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployerService, DashboardStats } from '../../../../core/services/employer.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = { totalJobs: 0, activeJobs: 0, totalApplications: 0, pendingReviews: 0 };
  loading = true;
  error = '';

  constructor(private employerService: EmployerService) {}

  ngOnInit() {
    this.employerService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load dashboard stats.';
        this.loading = false;
      }
    });
  }
}
