import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { JobService, CreateJobPostRequest, JobPost } from '../../../../core/services/job.service';

@Component({
  selector: 'app-post-job',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './post-job.component.html',
  styleUrls: ['./post-job.component.css']
})
export class PostJobComponent {
  jobData: CreateJobPostRequest = {
    title: '',
    description: '',
    requiredSkills: [],
    experienceRequired: '',
    educationRequired: '',
    location: '',
    salaryMin: undefined,
    salaryMax: undefined,
    jobType: '',
    applicationDeadline: '',
    numberOfOpenings: undefined
  };
  skillsInput = '';
  successMessage = '';
  errorMessage = '';

  constructor(private jobService: JobService, private router: Router) {}

  onSubmit() {
    if (this.skillsInput.trim()) {
      this.jobData.requiredSkills = this.skillsInput.split(',').map(s => s.trim());
    } else {
      this.jobData.requiredSkills = [];
    }

    this.jobService.createJobPost(this.jobData).subscribe({
      next: (createdJob: JobPost) => {
        this.successMessage = 'Job posted successfully!';
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/employer/manage-jobs']), 2000);
      },
      error: (err) => {
        console.error('Error posting job:', err);
        this.errorMessage = err.error?.message || 'Failed to post job. Please try again.';
        this.successMessage = '';
      }
    });
  }
  goBack() {
  this.router.navigate(['/employer/dashboard']);
}
}
