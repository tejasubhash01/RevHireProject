import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface DbCheck {
  userCount: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getEnums(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/enums`).pipe(
      map(response => response.data)
    );
  }

  getDbCheck(): Observable<DbCheck> {
    return this.http.get<any>(`${this.apiUrl}/db-check`).pipe(
      map(response => response.data)
    );
  }

  ping(): Observable<string> {
    return this.http.get<any>(`${this.apiUrl}/ping`).pipe(
      map(response => response.data)
    );
  }
}
