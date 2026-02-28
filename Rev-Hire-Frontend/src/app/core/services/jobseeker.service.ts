import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface JobSeekerProfile {
  id: number;
  userId: number;
  name: string;
  email: string;
  phone?: string;
  location?: string;
  employmentStatus?: string;
  headline?: string;
  summary?: string;
  resumeText?: ResumeText;
  resumeFile?: ResumeFile;
}

export interface ResumeText {
  id: number;
  objective?: string;
  education?: string;
  experience?: string;
  skills?: string;
  projects?: string;
  certifications?: string;
}

export interface ResumeFile {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadDate: string;
}

export interface CreateJobSeekerProfileRequest {
  headline?: string;
  summary?: string;
  employmentStatus?: string;
}

export interface UpdateResumeTextRequest {
  objective?: string;
  education?: string;
  experience?: string;
  skills?: string;
  projects?: string;
  certifications?: string;
}

@Injectable({ providedIn: 'root' })
export class JobseekerService {
  private apiUrl = `${environment.apiUrl}/jobseekers`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<JobSeekerProfile> {
    return this.http.get<any>(`${this.apiUrl}/profile/me`).pipe(
      map(response => response.data)
    );
  }

  createProfile(data: CreateJobSeekerProfileRequest): Observable<JobSeekerProfile> {
    return this.http.post<any>(`${this.apiUrl}/profile`, data).pipe(
      map(response => response.data)
    );
  }

  updateProfile(data: CreateJobSeekerProfileRequest): Observable<JobSeekerProfile> {
    return this.http.put<any>(`${this.apiUrl}/profile`, data).pipe(
      map(response => response.data)
    );
  }

  getResumeText(): Observable<ResumeText> {
    return this.http.get<any>(`${this.apiUrl}/resume/text/me`).pipe(
      map(response => response.data)
    );
  }

  updateResumeText(data: UpdateResumeTextRequest): Observable<ResumeText> {
    return this.http.post<any>(`${this.apiUrl}/resume/text`, data).pipe(
      map(response => response.data)
    );
  }

  uploadResumeFile(file: File): Observable<ResumeFile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.apiUrl}/resume/file`, formData).pipe(
      map(response => response.data)
    );
  }

  getResumeFile(): Observable<ResumeFile> {
    return this.http.get<any>(`${this.apiUrl}/resume/file/me`).pipe(
      map(response => response.data)
    );
  }


  getSeekerProfileByUserId(userId: number): Observable<JobSeekerProfile> {
    return this.http.get<any>(`${this.apiUrl}/profile/${userId}`).pipe(
      map(response => response.data)
    );
  }


  getSeekerProfileByProfileId(profileId: number): Observable<JobSeekerProfile> {
    return this.http.get<any>(`${this.apiUrl}/profile/by-profile/${profileId}`).pipe(
      map(response => response.data)
    );
  }
  downloadResumeFile(profileId: number): Observable<Blob> {
  return this.http.get(`${this.apiUrl}/resume/file/${profileId}`, { responseType: 'blob' });
}
}
