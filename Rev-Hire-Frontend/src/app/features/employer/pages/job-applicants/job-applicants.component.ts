import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  ApplicationService,
  ApplicationResponse,
  UpdateApplicationStatusRequest
} from '../../../../core/services/application.service';
import { JobseekerService, JobSeekerProfile } from '../../../../core/services/jobseeker.service';

@Component({
  selector: 'app-job-applicants',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './job-applicants.component.html',
  styleUrls: ['./job-applicants.component.css']
})
export class JobApplicantsComponent implements OnInit {
  jobId!: number;


  allApplicants: ApplicationResponse[] = [];
  filteredApplicants: ApplicationResponse[] = [];

  loading = true;
  error = '';

  search = '';
  skill = '';
  education = '';
  experience = '';
  status = '';
  fromDate = '';
  toDate = '';

  selectedApplication: ApplicationResponse | null = null;
  showNoteModal = false;
  noteContent = '';

  showProfileModal = false;
  selectedSeekerProfile: JobSeekerProfile | null = null;
  profileLoading = false;
  profileError = '';

  showStatusModal = false;
  statusUpdateData: UpdateApplicationStatusRequest = { status: '', employerNotes: '' };
  statusApplicationId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private applicationService: ApplicationService,
    private jobseekerService: JobseekerService
  ) {}

  ngOnInit() {
    this.jobId = Number(this.route.snapshot.paramMap.get('jobId'));
    this.loadApplicants();
  }

  loadApplicants() {
    this.loading = true;
    this.applicationService.getApplicationsForJob(this.jobId).subscribe({
      next: (data) => {
        this.allApplicants = data || [];
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load applicants.';
        this.loading = false;
      }
    });
  }

  applyFilters() {
    const q = (this.search || '').toLowerCase().trim();
    const skill = (this.skill || '').toLowerCase().trim();
    const edu = (this.education || '').toLowerCase().trim();
    const exp = (this.experience || '').toLowerCase().trim();
    const st = (this.status || '').toLowerCase().trim();

    const from = this.fromDate ? new Date(this.fromDate) : null;
    const to = this.toDate ? new Date(this.toDate) : null;
    if (to) to.setHours(23, 59, 59, 999);

    this.filteredApplicants = this.allApplicants.filter(a => {
      const name = (a.jobSeekerName || '').toLowerCase();

      const educationText = ((a as any).education || '').toLowerCase();
      const experienceText = ((a as any).experience || '').toLowerCase();

      const skillsText = (((a as any).skills ?? []) as string[]).join(' ').toLowerCase();

      const statusText = (a.status || '').toLowerCase();
      const applied = a.appliedDate ? new Date(a.appliedDate) : null;


      const matchesQ =
        !q ||
        name.includes(q) ||
        educationText.includes(q) ||
        experienceText.includes(q) ||
        skillsText.includes(q);

      if (!matchesQ) return false;

      if (skill && !skillsText.includes(skill)) return false;
      if (edu && !educationText.includes(edu)) return false;
      if (exp && !experienceText.includes(exp)) return false;

      if (st && statusText !== st) return false;

      if (from && (!applied || applied < from)) return false;
      if (to && (!applied || applied > to)) return false;

      return true;
    });
  }

  resetFilters() {
    this.search = '';
    this.skill = '';
    this.education = '';
    this.experience = '';
    this.status = '';
    this.fromDate = '';
    this.toDate = '';
    this.filteredApplicants = [...this.allApplicants];
  }

  viewProfile(profileId: number) {
    console.log('View profile clicked for profile ID:', profileId);
    this.profileLoading = true;
    this.profileError = '';
    this.jobseekerService.getSeekerProfileByProfileId(profileId).subscribe({
      next: (profile) => {
        console.log('Profile loaded:', profile);
        this.selectedSeekerProfile = profile;
        this.profileLoading = false;
        this.showProfileModal = true;
      },
      error: (err) => {
        console.error('Error loading profile:', err);
        this.profileError = 'Failed to load profile.';
        this.profileLoading = false;
      }
    });
  }

  downloadResumeFile(profileId: number) {
    console.log('Downloading resume for profile ID:', profileId);
    this.jobseekerService.downloadResumeFile(profileId).subscribe({
      next: (blob) => {
        console.log('Download successful, blob size:', blob.size);
        const a = document.createElement('a');
        const url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = '';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed with error:', err);
        alert('Failed to download file. Check console for details (F12).');
      }
    });
  }

  closeProfileModal() {
    this.showProfileModal = false;
    this.selectedSeekerProfile = null;
  }

  openStatusModal(application: ApplicationResponse) {
    this.statusApplicationId = application.id;
    this.statusUpdateData = {
      status: application.status,
      employerNotes: ''
    };
    this.showStatusModal = true;
  }

  closeStatusModal() {
    this.showStatusModal = false;
    this.statusApplicationId = null;
    this.statusUpdateData = { status: '', employerNotes: '' };
  }

  submitStatusUpdate() {
    if (!this.statusApplicationId) return;
    this.applicationService.updateApplicationStatus(this.statusApplicationId, this.statusUpdateData).subscribe({
      next: (updated) => {
        const index = this.allApplicants.findIndex(a => a.id === updated.id);
        if (index !== -1) this.allApplicants[index] = updated;


        this.applyFilters();
        this.closeStatusModal();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to update status.');
      }
    });
  }

  addNote(application: ApplicationResponse) {
    this.selectedApplication = application;
    this.noteContent = '';
    this.showNoteModal = true;
  }

  submitNote() {
    if (!this.selectedApplication || !this.noteContent.trim()) return;
    this.applicationService.addNote(this.selectedApplication.id, { note: this.noteContent }).subscribe({
      next: (updated) => {
        const index = this.allApplicants.findIndex(a => a.id === updated.id);
        if (index !== -1) this.allApplicants[index] = updated;


        this.applyFilters();
        this.closeNoteModal();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to add note.');
      }
    });
  }

  closeNoteModal() {
    this.showNoteModal = false;
    this.noteContent = '';
    this.selectedApplication = null;
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
