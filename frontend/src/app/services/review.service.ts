import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface CommentDTO {
  id: number;
  text: string;
  parentCommentId?: number;
  reviewId: number;
  userId: number;
  email: string;
  createdAt: string;
  replies?: CommentDTO[];
}

export interface ReviewDTO {
  id: number;
  facilityId: number;
  equipmentRating: number;
  staffRating: number;
  hygieneRating: number;
  spaceRating: number;
  comment: string;
  hidden: boolean;
  createdAt: string | [number, number, number, number, number, number, number];
  exercisesCount: number;
  rateId: number;
  userId: number;
  email: string;
  comments: CommentDTO[];
}

export interface FacilityDTO {
  id: number;
  name: string;
  city: string;
  address: string;
  description: string;
  totalRating: number;
  disciplines: string[];
  workDays: any[];
  reviews: ReviewDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  createReview(reviewDTO: ReviewDTO): Observable<{ message: string }> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.post<{ message: string }>(`${this.apiUrl}/create`, reviewDTO, { headers });
  }

  getAllReviews(): Observable<FacilityDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<FacilityDTO[]>(`${this.apiUrl}/all`, { headers });
  }

  getUserReviews(): Observable<ReviewDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<ReviewDTO[]>(`${this.apiUrl}/user`, { headers });
  }

  hideReview(reviewId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.patch(`${this.apiUrl}/hide/${reviewId}`, {}, { headers });
  }

  showReview(reviewId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.patch(`${this.apiUrl}/show/${reviewId}`, {}, { headers });
  }

  deleteReview(reviewId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.delete(`${this.apiUrl}/delete/${reviewId}`, { headers });
  }

  replyToComment(commentDTO: { text: string; parentCommentId: number }): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    return this.http.post(`${this.apiUrl}/reply`, commentDTO, { headers });
  }

  getReviewsSortedByRating(facilityId: number, ascending: boolean): Observable<ReviewDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const params = new HttpParams().set('ascending', ascending.toString());
    return this.http.get<ReviewDTO[]>(`${this.apiUrl}/facility/${facilityId}/sortByRating`, { headers, params })
      .pipe(map(reviews => reviews.map(review => this.convertReviewDate(review))));
  }

  getReviewsSortedByDate(facilityId: number, ascending: boolean): Observable<ReviewDTO[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const params = new HttpParams().set('ascending', ascending.toString());
    return this.http.get<ReviewDTO[]>(`${this.apiUrl}/facility/${facilityId}/sortByDate`, { headers, params })
      .pipe(map(reviews => reviews.map(review => this.convertReviewDate(review))));
  }

  private convertReviewDate(review: ReviewDTO): ReviewDTO {
    if (typeof review.createdAt === 'string') {
        review.createdAt = new Date(review.createdAt).toISOString().split('T')[0];
    } else if (Array.isArray(review.createdAt)) {
        const [year, month, day] = review.createdAt;
        review.createdAt = new Date(Date.UTC(year, month - 1, day)).toISOString().split('T')[0];
    }
    return review;
}
}
