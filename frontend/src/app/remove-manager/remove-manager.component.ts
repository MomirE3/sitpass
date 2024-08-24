import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ManagerService } from '../services/manager.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { Facility } from '../services/facility.model';

@Component({
  selector: 'app-remove-manager',
  templateUrl: './remove-manager.component.html',
  styleUrls: ['./remove-manager.component.scss']
})
export class RemoveManagerComponent implements OnInit {
  removeManagerForm: FormGroup;
  facilities: Facility[] = [];
  managerName: string | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private managerService: ManagerService,
    private authService: AuthService,
    private router: Router
  ) {
    this.removeManagerForm = this.fb.group({
      facilityId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isAdmin()) {
      this.authService.logout();
      this.router.navigate(['/login']);
    } else {
      this.loadActiveFacilities();
    }
  }

  loadActiveFacilities(): void {
    this.managerService.getActiveFacilities().subscribe(
      (facilities) => {
        this.facilities = facilities;
      },
      (error) => {
        console.error('Error loading active facilities', error);
      }
    );
  }

  onFacilityChange(): void {
    const facilityId = this.removeManagerForm.get('facilityId')?.value;
    if (facilityId) {
      this.managerService.getManagerByFacilityId(facilityId).subscribe(
        (manager) => {
          this.managerName = `${manager.name} ${manager.surname}`;
        },
        (error) => {
          this.managerName = null;
          console.error('Error fetching manager details', error);
        }
      );
    } else {
      this.managerName = null;
    }
  }

  onSubmit(): void {
    if (this.removeManagerForm.valid) {
      const facilityId = this.removeManagerForm.get('facilityId')?.value;
      this.managerService.removeManager(facilityId).subscribe(
        (response) => {
          this.successMessage = response.message || 'Manager removed successfully';
          this.resetForm();
          this.loadActiveFacilities();
        },
        (error) => {
          this.errorMessage = error.error || 'Error removing manager';
        }
      );
    }
  }

  resetForm(): void {
    this.removeManagerForm.reset();
    this.removeManagerForm.patchValue({
      facilityId: ''
    });
    this.managerName = null
  }
}
