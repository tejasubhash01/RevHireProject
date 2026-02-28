import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobseekerService, ResumeText, UpdateResumeTextRequest, ResumeFile } from '../../../../core/services/jobseeker.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-resume',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resume.component.html',
  styleUrls: ['./resume.component.css']
})
export class ResumeComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;

  resumeText: ResumeText = {
    id: 0,
    objective: '',
    education: '',
    experience: '',
    skills: '',
    projects: '',
    certifications: ''
  };
  loading = true;
  loadError = '';
  saveSuccess = '';
  saveError = '';


  resumeFile: ResumeFile | null = null;
  selectedFile: File | null = null;
  uploadLoading = false;
  uploadSuccess = '';
  uploadError = '';

  constructor(private jobseekerService: JobseekerService,private router: Router) {}

  ngOnInit() {
    this.loadResumeText();
    this.loadResumeFile();
  }

  loadResumeText() {
    this.jobseekerService.getResumeText().subscribe({
      next: (data) => {
        console.log('Resume loaded:', data);
        if (data) {
          this.resumeText = data;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading resume:', err);
        this.loadError = 'Could not load existing resume. You can create a new one below.';
        this.loading = false;
      }
    });
  }

  loadResumeFile() {
    this.jobseekerService.getResumeFile().subscribe({
      next: (data) => {
        this.resumeFile = data;
      },
      error: (err) => {

        if (err.status !== 404) {
          console.error('Error loading resume file:', err);
        }
      }
    });
  }

  onSubmit() {
    console.log('Submitting resume:', this.resumeText);
    this.saveSuccess = '';
    this.saveError = '';
    const updateData: UpdateResumeTextRequest = {
      objective: this.resumeText.objective,
      education: this.resumeText.education,
      experience: this.resumeText.experience,
      skills: this.resumeText.skills,
      projects: this.resumeText.projects,
      certifications: this.resumeText.certifications
    };
    this.jobseekerService.updateResumeText(updateData).subscribe({
      next: (updated) => {
        console.log('Resume saved:', updated);
        this.resumeText = updated;
        this.saveSuccess = 'Resume saved successfully.';
      },
      error: (err) => {
        console.error('Error saving resume:', err);
        this.saveError = err.error?.message || 'Failed to save resume.';
      }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (!allowedTypes.includes(file.type)) {
        this.uploadError = 'Only PDF and DOCX files are allowed.';
        this.selectedFile = null;
        return;
      }
      if (file.size > 2 * 1024 * 1024) {
        this.uploadError = 'File size must be less than 2MB.';
        this.selectedFile = null;
        return;
      }
      this.selectedFile = file;
      this.uploadError = '';
    }
  }

  uploadFile() {
    if (!this.selectedFile) return;
    this.uploadLoading = true;
    this.uploadSuccess = '';
    this.uploadError = '';
    this.jobseekerService.uploadResumeFile(this.selectedFile).subscribe({
      next: (data) => {
        this.resumeFile = data;
        this.uploadSuccess = 'File uploaded successfully.';
        this.uploadLoading = false;
        this.selectedFile = null;
        this.fileInput.nativeElement.value = '';
      },
      error: (err) => {
        console.error('Upload error:', err);
        this.uploadError = err.error?.message || 'Upload failed.';
        this.uploadLoading = false;
      }
    });
  }
    goBack() {
    this.router.navigate(['/jobseeker/dashboard']);
  }
}
