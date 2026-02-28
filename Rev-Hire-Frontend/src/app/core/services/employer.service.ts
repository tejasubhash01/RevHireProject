import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface EmployerProfile {
  id: number;
  userId: number;
  companyName: string;
  industry: string;
  companySize: string;
  companyDescription: string;
  website: string;
  location: string;
}

export interface UpdateEmployerProfileRequest {
  companyName?: string;
  industry?: string;
  companySize?: string;
  companyDescription?: string;
  website?: string;
  location?: string;
}

export interface DashboardStats {
  totalJobs: number;
  activeJobs: number;
  totalApplications: number;
  pendingReviews: number;
}

export interface JobPostSummary {
  id: number;
  title: string;
  isActive: boolean;
  isFilled: boolean;
  createdAt: string;
  applicationDeadline: string;
}

@Injectable({ providedIn: 'root' })
export class EmployerService {
  private apiUrl = `${environment.apiUrl}/employers`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<EmployerProfile> {
    return this.http.get<any>(`${this.apiUrl}/profile/me`).pipe(
      map(response => response.data)
    );
  }

  updateProfile(data: UpdateEmployerProfileRequest): Observable<EmployerProfile> {
    return this.http.put<any>(`${this.apiUrl}/profile`, data).pipe(
      map(response => response.data)
    );
  }

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<any>(`${this.apiUrl}/dashboard`).pipe(
      map(response => response.data)
    );
  }

  getMyJobs(): Observable<JobPostSummary[]> {
    return this.http.get<any>(`${environment.apiUrl}/jobs/my`).pipe(
      map(response => response.data.content)
    );
  }
}
