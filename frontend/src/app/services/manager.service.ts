import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facility } from './facility.model';
import { AssignManagerDTO } from './assign-manager.dto';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {
  private facilityApiUrl = 'http://localhost:8080/api/facilities';
  private managerApiUrl = 'http://localhost:8080/api/manager';

  constructor(private http: HttpClient) {}

  getInactiveFacilities(): Observable<Facility[]> {
    return this.http.get<Facility[]>(`${this.facilityApiUrl}/inactive`);
  }

  getActiveFacilities(): Observable<Facility[]> {
    return this.http.get<Facility[]>(`${this.facilityApiUrl}/active`);
  }

  assignManager(assignManagerDTO: AssignManagerDTO): Observable<any> {
    return this.http.post<any>(`${this.managerApiUrl}/assign`, assignManagerDTO);
  }

  removeManager(facilityId: number): Observable<any> {
    return this.http.delete<any>(`${this.managerApiUrl}/remove?facilityId=${facilityId}`);
  }

  getManagerByFacilityId(facilityId: number): Observable<any> {
    return this.http.get<any>(`${this.managerApiUrl}/manager?facilityId=${facilityId}`);
  }
}