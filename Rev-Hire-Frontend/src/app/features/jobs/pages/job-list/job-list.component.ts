import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  JobService,
  JobPost,
  JobSearchFilter
} from '../../../../core/services/job.service';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnInit {
  jobs: JobPost[] = [];

  filter: JobSearchFilter = {
    title: '',
    location: '',
    jobType: '',
    page: 0,
    size: 10
  };

  experienceRange = '';
  salaryRange = '';

  loading = false;
  error = '';
  page = 0;
  totalPages = 0;

  constructor(private jobService: JobService) {}

  ngOnInit() {
    this.search();
  }

search() {
  this.applyExperienceFilter();
  this.applySalaryFilter();

  this.loading = true;
  this.jobService.searchJobs(this.filter).subscribe({
    next: res => {
      this.jobs = res.content;
      this.totalPages = res.totalPages;
      this.page = res.number;
      this.loading = false;
    },
    error: (err) => {
      console.error('Job search error:', err);
      this.error = err.error?.message || 'Failed to load jobs';
      this.loading = false;
    }
  });
}

  applyExperienceFilter() {
    if (!this.experienceRange) {
      this.filter.minExp = undefined;
      this.filter.maxExp = undefined;
      return;
    }
    const [min, max] = this.experienceRange.split('-').map(Number);
    this.filter.minExp = min;
    this.filter.maxExp = max ?? 50;
  }

  applySalaryFilter() {
    if (!this.salaryRange) {
      this.filter.salaryMin = undefined;
      this.filter.salaryMax = undefined;
      return;
    }
    const [min, max] = this.salaryRange.split('-').map(Number);
    this.filter.salaryMin = min;
    this.filter.salaryMax = max ?? 99999999;
  }

  changePage(newPage: number) {
    this.filter.page = newPage;
    this.search();
  }
}
