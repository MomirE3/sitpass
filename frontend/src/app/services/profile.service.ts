import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Profile } from '../profile/profile.model';
import { AuthService } from './auth.service';
import { catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8080/api/profile';

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

  getProfile(): Observable<Profile> {
    const headers = this.getHeaders();
    return this.http.get<Profile>(this.apiUrl, { headers }).pipe(
      tap(data => console.log('Profile fetched:', data)),
      catchError(this.handleError)
    );
  }

  updateProfile(profileData: Profile): Observable<any> {
    const headers = this.getHeaders();
    return this.http.patch<any>(this.apiUrl, profileData, { headers }).pipe(
      tap(data => console.log('Profile updated:', data)),
      catchError(this.handleError)
    );
  }

  uploadProfilePicture(file: File): Observable<any> {
    const headers = this.getHeaders();
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    return this.http.patch<any>(this.apiUrl, formData, { headers }).pipe(
      tap(data => console.log('Profile picture uploaded:', data)),
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error('An error occurred; please try again later.'));
  }
}
