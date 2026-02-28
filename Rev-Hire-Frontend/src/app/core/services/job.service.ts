import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface JobPost {
  id: number;
  title: string;
  description: string;
  requiredSkills?: string[];
  experienceRequired?: string;
  educationRequired?: string;
  location?: string;
  salaryMin?: number;
  salaryMax?: number;
  jobType?: string;
  applicationDeadline?: string;
  numberOfOpenings?: number;
  isActive?: boolean;
  isFilled?: boolean;
  employerId?: number;
  companyName?: string;
}

export interface CreateJobPostRequest {
  title: string;
  description: string;
  requiredSkills?: string[];
  experienceRequired?: string;
  educationRequired?: string;
  location?: string;
  salaryMin?: number;
  salaryMax?: number;
  jobType: string;
  applicationDeadline?: string;
  numberOfOpenings?: number;
}

export interface UpdateJobPostRequest {
  title?: string;
  description?: string;
  requiredSkills?: string[];
  experienceRequired?: string;
  educationRequired?: string;
  location?: string;
  salaryMin?: number;
  salaryMax?: number;
  jobType?: string;
  applicationDeadline?: string;
  numberOfOpenings?: number;
  isActive?: boolean;
  isFilled?: boolean;
}

export interface JobSearchFilter {
  title?: string;
  location?: string;
  company?: string;
  jobType?: string;
  minExp?: number;
  maxExp?: number;
  salaryMin?: number;
  salaryMax?: number;
  datePosted?: string;
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class JobService {
  private apiUrl = `${environment.apiUrl}/jobs`;

  constructor(private http: HttpClient) {}

  searchJobs(filter: JobSearchFilter): Observable<PageResponse<JobPost>> {
    let params = new HttpParams();
    Object.entries(filter).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, value.toString());
      }
    });
    return this.http.get<any>(`${this.apiUrl}/search`, { params }).pipe(
      map(response => response.data)
    );
  }

  getJobById(id: number): Observable<JobPost> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  createJobPost(data: CreateJobPostRequest): Observable<JobPost> {
    return this.http.post<any>(this.apiUrl, data).pipe(
      map(response => response.data)
    );
  }

  updateJobPost(id: number, data: UpdateJobPostRequest): Observable<JobPost> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data).pipe(
      map(response => response.data)
    );
  }

  deleteJobPost(id: number): Observable<void> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  closeJobPost(id: number): Observable<void> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/close`, {}).pipe(
      map(response => response.data)
    );
  }

  reopenJobPost(id: number): Observable<void> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/reopen`, {}).pipe(
      map(response => response.data)
    );
  }

  markJobFilled(id: number): Observable<void> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/mark-filled`, {}).pipe(
      map(response => response.data)
    );
  }


}
