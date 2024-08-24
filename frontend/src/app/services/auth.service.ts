import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string;
  type: string;
  exp: number;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api'; // Corrected API base URL
  private userType: string = '';
  private userId: number | undefined;

  constructor(private http: HttpClient) {}

  isManagerOrAdmin(): Observable<boolean> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    return this.http.get<boolean>(`${this.apiUrl}/facilities/isManagerOrAdmin`, { headers });
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/users/login`, { email, password });
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
    const decodedToken: DecodedToken = jwtDecode(token);
    console.log('Decoded Token in setToken:', decodedToken);
    if (decodedToken.userId !== undefined) {
      this.userId = decodedToken.userId;
    } else {
      console.error('User ID not found in token');
    }
    this.userType = decodedToken.type;
    console.log('Token set:', token);
    console.log('User ID set to:', this.userId);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserType(): string {
    if (!this.userType) {
      const token = localStorage.getItem('token');
      if (token) {
        const decodedToken: DecodedToken = jwtDecode(token);
        this.userType = decodedToken.type;
        console.log('Decoded Token in getUserType:', decodedToken);
      }
    }
    return this.userType;
  }

  getUserId(): number | undefined {
    if (this.userId === undefined) {
      const token = localStorage.getItem('token');
      if (token) {
        const decodedToken: DecodedToken = jwtDecode(token);
        if (decodedToken.userId !== undefined) {
          this.userId = decodedToken.userId;
        } else {
          console.error('User ID not found in token');
        }
        console.log('Decoded Token in getUserId:', decodedToken);
      }
    }
    console.log('Returning userId:', this.userId);
    return this.userId;
  }

  isTokenValid(token: string): boolean {
    try {
      const decodedToken: DecodedToken = jwtDecode(token);
      const currentTime = Math.floor(new Date().getTime() / 1000);
      console.log('Current time:', currentTime, 'Token expiry:', decodedToken.exp);
      return decodedToken.exp > currentTime;
    } catch (error) {
      console.error('Invalid token:', error);
      return false;
    }
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('token');
    return token ? this.isTokenValid(token) : false;
  }

  isUser(): boolean {
    const userType = this.getUserType();
    console.log('User type in isUser:', userType);
    return userType.toLowerCase() === 'user';
  }

  isAdmin(): boolean {
    const userType = this.getUserType();
    console.log('User type in isAdministrator:', userType);
    return userType.toLowerCase() === 'administrator';
  }

  logout(): void {
    localStorage.removeItem('token');
    this.userType = '';
    this.userId = undefined;
  }
}
