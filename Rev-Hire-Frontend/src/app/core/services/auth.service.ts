import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  phone?: string;
  location?: string;
  role: 'JOB_SEEKER' | 'EMPLOYER';
  employmentStatus?: string;
  companyName?: string;
  industry?: string;
  companySize?: string;
  companyDescription?: string;
  website?: string;

  securityQuestion?: string;
  securityAnswer?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      map(response => response.data),
      tap(res => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('role', res.role);
          localStorage.setItem('email', res.email);
          localStorage.setItem('name', res.name);
        }
      })
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${this.apiUrl}/register`, data).pipe(
      map(response => response.data),
      tap(res => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('role', res.role);
          localStorage.setItem('email', res.email);
          localStorage.setItem('name', res.name);
        }
      })
    );
  }

 logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.clear();
    }
    this.router.navigateByUrl('/login', { replaceUrl: true });
  }

  isLoggedIn(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('token');
    }
    return false;
  }

  getRole(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('role');
    }
    return null;
  }

  forgotPassword(email: string): Observable<{ question: string }> {
    return this.http.post<any>(`${this.apiUrl}/forgot-password`, { email }).pipe(
      map(response => response.data)
    );
  }

  verifyAnswer(email: string, answer: string): Observable<{ token: string }> {
    return this.http.post<any>(`${this.apiUrl}/verify-answer`, { email, answer }).pipe(
      map(response => response.data)
    );
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<any>(`${this.apiUrl}/reset-password`, { token, newPassword }).pipe(
      map(response => response.data)
    );
  }
}
