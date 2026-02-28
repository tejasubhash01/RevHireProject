import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface FavouriteJobResponse {
  id: number;
  jobPost: {
    id: number;
    title: string;
    companyName: string;
    location: string;
    jobType: string;
  };
}

@Injectable({ providedIn: 'root' })
export class FavouritesService {
  private apiUrl = `${environment.apiUrl}/favourites`;

  constructor(private http: HttpClient) {}

  getMyFavourites(): Observable<FavouriteJobResponse[]> {
    return this.http.get<any>(`${this.apiUrl}/my`).pipe(
      map(response => response.data)
    );
  }

  addFavourite(jobId: number): Observable<void> {
    return this.http.post<any>(`${this.apiUrl}/${jobId}`, {}).pipe(
      map(response => response.data)
    );
  }

  removeFavourite(jobId: number): Observable<void> {
    return this.http.delete<any>(`${this.apiUrl}/${jobId}`).pipe(
      map(response => response.data)
    );
  }

}
