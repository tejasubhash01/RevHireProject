import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployerService, JobPostSummary } from '../../../../core/services/employer.service';
import { JobService } from '../../../../core/services/job.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-manage-jobs',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './manage-jobs.component.html',
  styleUrls: ['./manage-jobs.component.css']
})
export class ManageJobsComponent implements OnInit {
  jobs: JobPostSummary[] = [];
  loading = true;
  error = '';

  showConfirmModal = false;
  confirmAction: 'close' | 'reopen' | 'markFilled' | 'delete' | null = null;
  selectedJobId: number | null = null;
  confirmTitle = '';
  confirmMessage = '';

  constructor(
    private employerService: EmployerService,
    private jobService: JobService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadJobs();
  }

  loadJobs() {
    this.employerService.getMyJobs().subscribe({
      next: (jobs) => {
        this.jobs = jobs;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load jobs.';
        this.loading = false;
      }
    });
  }

  openConfirmModal(action: 'close' | 'reopen' | 'markFilled' | 'delete', jobId: number, jobTitle: string) {
    this.confirmAction = action;
    this.selectedJobId = jobId;
    switch (action) {
      case 'close':
        this.confirmTitle = 'Close Job';
        this.confirmMessage = `Are you sure you want to close the job "${jobTitle}"? It will no longer be visible to applicants.`;
        break;
      case 'reopen':
        this.confirmTitle = 'Reopen Job';
        this.confirmMessage = `Reopen "${jobTitle}"? It will become active again.`;
        break;
      case 'markFilled':
        this.confirmTitle = 'Mark Job as Filled';
        this.confirmMessage = `Mark "${jobTitle}" as filled? This will close it and mark as filled.`;
        break;
      case 'delete':
        this.confirmTitle = 'Delete Job';
        this.confirmMessage = `Are you sure you want to delete "${jobTitle}"? This action cannot be undone.`;
        break;
    }
    this.showConfirmModal = true;
  }

  closeConfirmModal() {
    this.showConfirmModal = false;
    this.confirmAction = null;
    this.selectedJobId = null;
  }

  confirmActionHandler() {
    if (!this.confirmAction || !this.selectedJobId) return;
    switch (this.confirmAction) {
      case 'close':
        this.jobService.closeJobPost(this.selectedJobId).subscribe({
          next: () => this.loadJobs(),
          error: (err) => console.error(err)
        });
        break;
      case 'reopen':
        this.jobService.reopenJobPost(this.selectedJobId).subscribe({
          next: () => this.loadJobs(),
          error: (err) => console.error(err)
        });
        break;
      case 'markFilled':
        this.jobService.markJobFilled(this.selectedJobId).subscribe({
          next: () => this.loadJobs(),
          error: (err) => console.error(err)
        });
        break;
      case 'delete':
        this.jobService.deleteJobPost(this.selectedJobId).subscribe({
          next: () => this.loadJobs(),
          error: (err) => console.error(err)
        });
        break;
    }
    this.closeConfirmModal();
  }

  getStatus(job: JobPostSummary): string {
    if (job.isFilled) return 'Filled';
    if (job.isActive) return 'Active';
    return 'Closed';
  }

  goToDashboard() {
    this.router.navigate(['/employer/dashboard']);
  }
}
