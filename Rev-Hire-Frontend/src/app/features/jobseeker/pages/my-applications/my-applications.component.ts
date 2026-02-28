import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApplicationService, ApplicationResponse } from '../../../../core/services/application.service';

@Component({
  selector: 'app-my-applications',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './my-applications.component.html',
  styleUrls: ['./my-applications.component.css']
})
export class MyApplicationsComponent implements OnInit {
  applications: ApplicationResponse[] = [];
  loading = true;
  error = '';

  showWithdrawModal = false;
  withdrawApplicationId: number | null = null;
  withdrawReason = '';

  constructor(private applicationService: ApplicationService) {}

  ngOnInit() {
    this.loadApplications();
  }

  loadApplications() {
    this.applicationService.getMyApplications().subscribe({
      next: (data) => {
        this.applications = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load applications.';
        this.loading = false;
      }
    });
  }

  openWithdrawModal(applicationId: number) {
    this.withdrawApplicationId = applicationId;
    this.withdrawReason = '';
    this.showWithdrawModal = true;
  }

  closeWithdrawModal() {
    this.showWithdrawModal = false;
    this.withdrawApplicationId = null;
    this.withdrawReason = '';
  }

  confirmWithdraw() {
    if (!this.withdrawApplicationId) return;
    this.applicationService.withdrawApplication(this.withdrawApplicationId, this.withdrawReason || undefined).subscribe({
      next: () => {
        this.closeWithdrawModal();
        this.loadApplications();
      },
      error: (err) => {
        console.error(err);
        alert('Withdrawal failed.');
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'APPLIED': return 'status-applied';
      case 'UNDER_REVIEW': return 'status-review';
      case 'SHORTLISTED': return 'status-shortlisted';
      case 'REJECTED': return 'status-rejected';
      case 'WITHDRAWN': return 'status-withdrawn';
      default: return '';
    }
  }
}
