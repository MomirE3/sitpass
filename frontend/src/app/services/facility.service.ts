import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facility } from './facility.model';

@Injectable({
  providedIn: 'root'
})
export class FacilityService {
  private apiUrl = 'http://localhost:8080/api/facilities';
  private disciplinesUrl = 'http://localhost:8080/api/disciplines/names';
  private eligibleFacilitiesUrl = 'http://localhost:8080/api/facilities/eligibleForReview';

  constructor(private http: HttpClient) {}

  getFacilities(): Observable<Facility[]> {
    return this.http.get<Facility[]>(this.apiUrl, this.getHttpOptions());
  }

  getNewFacilities(page: number, size: number): Observable<any> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/newFacilities`, { ...this.getHttpOptions(), params });
  }


  getEligibleFacilitiesForReview(): Observable<Facility[]> {
    return this.http.get<Facility[]>(this.eligibleFacilitiesUrl, this.getHttpOptions());
  }

  getFacility(id: number): Observable<Facility> {
    return this.http.get<Facility>(`${this.apiUrl}/${id}`, this.getHttpOptions());
  }

  getHomepageData(): Observable<any> {
    return this.http.get(`${this.apiUrl}/homepage`);
  }

  getNumberOfVisits(facilityId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/visits/${facilityId}`, this.getHttpOptions());
  }

  createFacilityWithImages(facilityData: FormData): Observable<Facility> {
    return this.http.post<Facility>(`${this.apiUrl}/createWithImages`, facilityData, this.getHttpOptions(false));
  }

  updateFacility(id: number, facility: any, options?: { headers?: HttpHeaders }): Observable<Facility> {
    return this.http.patch<Facility>(`${this.apiUrl}/${id}/updateFacilityDetails`, facility, options);
  }
  
  updateFacilityImages(id: number, formData: FormData, options?: { headers?: HttpHeaders }): Observable<Facility> {
    return this.http.patch<Facility>(`${this.apiUrl}/${id}/updateFacilityImages`, formData, options);
}


  filterFacilities(disciplines?: string[], minRating?: number | null, maxRating?: number | null, fromTime?: string, untilTime?: string, cities?: string[]): Observable<Facility[]> {
    let params = new HttpParams();
    if(disciplines && disciplines.length > 0){
      disciplines.forEach(discipline => {
        params = params.append('names', discipline)
      })
    }
    if (minRating != null) {
      params = params.append('minRating', minRating.toString());
    }
    if (maxRating != null) {
      params = params.append('maxRating', maxRating.toString());
    }
    if (fromTime) {
      params = params.append('fromTime', fromTime);
    }
    if (untilTime) {
      params = params.append('untilTime', untilTime);
    }
    if (cities && cities.length > 0) {
      cities.forEach(city => {
        params = params.append('cities', city);
      });
    }

    return this.http.get<Facility[]>(`${this.apiUrl}/filter`, { ...this.getHttpOptions(), params });
  }

  getUniqueCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/cities`, this.getHttpOptions());
  }

  getDisciplines(): Observable<string[]> {
    return this.http.get<string[]>(this.disciplinesUrl, this.getHttpOptions());
  }

  isManagerOfFacility(facilityId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${facilityId}/isManager`, this.getHttpOptions());
  }

  private getHttpOptions(includeJson: boolean = true) {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    if (includeJson) {
      headers = headers.set('Content-Type', 'application/json');
    }
    return { headers };
  }
}
