import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface NotificationResponse {
  id: number;
  message: string;
  isRead: boolean;
  type: string;
  link: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<any>(`${this.apiUrl}/my`).pipe(
      map(response => response.data)
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/read`, {}).pipe(
      map(response => response.data)
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.patch<any>(`${this.apiUrl}/read-all`, {}).pipe(
      map(response => response.data)
    );
  }
}
