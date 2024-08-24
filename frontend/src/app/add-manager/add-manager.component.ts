import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ManagerService } from '../services/manager.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { Facility } from '../services/facility.model';
import { AssignManagerDTO } from '../services/assign-manager.dto';
import { UserService } from '../services/user.service';
import { User } from '../services/user.model';

@Component({
  selector: 'app-add-manager',
  templateUrl: './add-manager.component.html',
  styleUrls: ['./add-manager.component.scss']
})
export class AddManagerComponent implements OnInit {
  addManagerForm: FormGroup;
  facilities: Facility[] = [];
  users: User[] = [];
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private managerService: ManagerService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.addManagerForm = this.fb.group({
      userId: ['', Validators.required],
      facilityId: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isAdmin()) {
      this.authService.logout();
      this.router.navigate(['/login']);
    } else {
      this.loadInactiveFacilities();
      this.loadUsers();
    }
  }

  loadInactiveFacilities(): void {
    this.managerService.getInactiveFacilities().subscribe(
      (facilities) => {
        this.facilities = facilities;
      },
      (error) => {
        console.error('Error loading inactive facilities', error);
      }
    );
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(
      (users) => {
        this.users = users;
      },
      (error) => {
        console.error('Error loading users', error);
      }
    );
  }

  onSubmit(): void {
    if (this.addManagerForm.valid) {
      const assignManagerDTO: AssignManagerDTO = this.addManagerForm.value;
      this.managerService.assignManager(assignManagerDTO).subscribe(
        (response) => {
          this.successMessage = response.message || 'Manager assigned successfully';
          this.errorMessage = null; 
          this.addManagerForm.reset(); 
          this.loadInactiveFacilities();
        },
        (error) => {
          this.errorMessage = error.error || 'Error assigning manager';
          this.successMessage = null; 
        }
      );
    }
  }
}
