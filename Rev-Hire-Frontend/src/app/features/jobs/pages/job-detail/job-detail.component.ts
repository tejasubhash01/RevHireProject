import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JobService, JobPost } from '../../../../core/services/job.service';
import { ApplicationService, ApplyJobRequest } from '../../../../core/services/application.service';
import { FavouritesService } from '../../../../core/services/favourites.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './job-detail.component.html',
  styleUrls: ['./job-detail.component.css']
})
export class JobDetailComponent implements OnInit {
  job: JobPost | null = null;
  loading = true;
  error = '';
  showApplyForm = false;
  coverLetter = '';
  applySuccess = '';
  applyError = '';


  isFavourite = false;
  favouriteId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private jobService: JobService,
    private applicationService: ApplicationService,
    private favouritesService: FavouritesService,
    public authService: AuthService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadJob(id);
  }

  loadJob(id: number) {
    this.loading = true;
    this.jobService.getJobById(id).subscribe({
      next: (job) => {
        this.job = job;
        this.loading = false;
        if (this.authService.isLoggedIn() && this.authService.getRole() === 'JOB_SEEKER') {
          this.checkIfFavourite(job.id);
        }
      },
      error: (err) => {
        this.error = 'Failed to load job details.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  checkIfFavourite(jobId: number) {
    this.favouritesService.getMyFavourites().subscribe({
      next: (favourites) => {
        const fav = favourites.find(f => f.jobPost.id === jobId);
        if (fav) {
          this.isFavourite = true;
          this.favouriteId = fav.id;
        } else {
          this.isFavourite = false;
          this.favouriteId = null;
        }
      },
      error: (err) => console.error('Error checking favourites:', err)
    });
  }

  toggleFavourite() {
    if (!this.job) return;
    if (this.isFavourite) {

      this.favouritesService.removeFavourite(this.job.id).subscribe({
        next: () => {
          this.isFavourite = false;
          this.favouriteId = null;
        },
        error: (err) => {
          console.error('Error removing favourite:', err);
          alert('Failed to remove from favourites.');
        }
      });
    } else {

      this.favouritesService.addFavourite(this.job.id).subscribe({
        next: (response) => {
          this.isFavourite = true;

        },
        error: (err) => {
          console.error('Error adding favourite:', err);
          alert('Failed to add to favourites.');
        }
      });
    }
  }

  toggleApplyForm() {
    this.showApplyForm = !this.showApplyForm;
    this.applySuccess = '';
    this.applyError = '';
  }
  submitApplication() {
  if (!this.job) return;
  const request: ApplyJobRequest = {
    jobPostId: this.job.id,
    coverLetter: this.coverLetter
  };
  console.log('Submitting application:', request);
  this.applicationService.applyForJob(request).subscribe({
    next: (response) => {
      console.log('Apply success:', response);
      this.applySuccess = 'Application submitted successfully!';
      this.showApplyForm = false;
      this.coverLetter = '';
    },
    error: (err) => {
      console.error('Apply error:', err);
      this.applyError = err.error?.message || 'Application failed.';
    }
  });
}
}
