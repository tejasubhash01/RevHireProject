import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ApplyJobRequest {
  jobPostId: number;
  coverLetter?: string;
}

export interface ApplicationResponse {
  id: number;
  jobPostId: number;
  jobTitle: string;
  companyName: string;
  jobSeekerId: number;
  jobSeekerName: string;
  status: string;
  coverLetter?: string;
  appliedDate: string;
  education?: string;
  experience?: string;
  skills?: string[];
  employerNotes?: string;
}

export interface UpdateApplicationStatusRequest {
  status: string;
  employerNotes?: string;
}

export interface NoteRequest {
  note: string;
}

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private apiUrl = `${environment.apiUrl}/applications`;

  constructor(private http: HttpClient) {}

  applyForJob(data: ApplyJobRequest): Observable<ApplicationResponse> {
    return this.http.post<any>(`${this.apiUrl}/apply`, data).pipe(
      map(response => response.data)
    );
  }

  getMyApplications(): Observable<ApplicationResponse[]> {
    return this.http.get<any>(`${this.apiUrl}/my`).pipe(
      map(response => response.data)
    );
  }

  withdrawApplication(applicationId: number, reason?: string): Observable<void> {
    const body = reason ? { reason } : {};
    return this.http.patch<any>(`${this.apiUrl}/${applicationId}/withdraw`, body).pipe(
      map(response => response.data)
    );
  }

  getApplicationsForJob(jobId: number): Observable<ApplicationResponse[]> {
    return this.http.get<any>(`${this.apiUrl}/job/${jobId}`).pipe(
      map(response => response.data)
    );
  }

  updateApplicationStatus(applicationId: number, data: UpdateApplicationStatusRequest): Observable<ApplicationResponse> {
    return this.http.patch<any>(`${this.apiUrl}/${applicationId}/status`, data).pipe(
      map(response => response.data)
    );
  }

  addNote(applicationId: number, data: NoteRequest): Observable<ApplicationResponse> {
    return this.http.patch<any>(`${this.apiUrl}/${applicationId}/note`, data).pipe(
      map(response => response.data)
    );
  }

}
