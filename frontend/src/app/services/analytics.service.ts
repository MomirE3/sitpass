import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private baseUrl = 'http://localhost:8080/api/analytics/facility';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    if (!token) {
      console.error('No token found in local storage');
      throw new Error('No token found in local storage');
    }
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getWeeklyAnalytics(facilityId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get<any>(`${this.baseUrl}/${facilityId}/weekly`, { headers }).pipe(
      tap(data => console.log('Weekly Analytics fetched:', data)),
      catchError(this.handleError)
    );
  }

  getMonthlyAnalytics(facilityId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get<any>(`${this.baseUrl}/${facilityId}/monthly`, { headers }).pipe(
      tap(data => console.log('Monthly Analytics fetched:', data)),
      catchError(this.handleError)
    );
  }

  getYearlyAnalytics(facilityId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get<any>(`${this.baseUrl}/${facilityId}/yearly`, { headers }).pipe(
      tap(data => console.log('Yearly Analytics fetched:', data)),
      catchError(this.handleError)
    );
  }

  getCustomAnalytics(facilityId: number, startDate: string, endDate: string): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get<any>(`${this.baseUrl}/${facilityId}/custom`, {
      headers,
      params: { startDate, endDate }
    }).pipe(
      tap(data => console.log('Custom Analytics fetched:', data)),
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error('An error occurred; please try again later.'));
  }
}
