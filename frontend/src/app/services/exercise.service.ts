import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Exercise {
  userId: number;
  facilityId: number;
  from: string;
  until: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private apiUrl = 'http://localhost:8080/api/exercises';

  constructor(private http: HttpClient) {}

  createExercise(exercise: Exercise): Observable<string> {
    return this.http.post(this.apiUrl + '/create', exercise, { responseType: 'text' });
  }
}
