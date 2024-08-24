import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { FacilityService } from '../services/facility.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-create-facility',
  templateUrl: './create-facility.component.html',
  styleUrls: ['./create-facility.component.scss']
})
export class CreateFacilityComponent implements OnInit {
  facilityForm: FormGroup;
  allDays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  constructor(
    private fb: FormBuilder,
    private facilityService: FacilityService,
    private router: Router,
    private authService: AuthService
  ) {
    this.facilityForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      disciplines: this.fb.array([this.fb.control('')]),
      workDays: this.fb.array([this.createWorkDay()]),
      images: this.fb.array([])
    });
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isAdmin()) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  get disciplines() {
    return this.facilityForm.get('disciplines') as FormArray;
  }

  get workDays() {
    return this.facilityForm.get('workDays') as FormArray;
  }

  get images() {
    return this.facilityForm.get('images') as FormArray;
  }

  createWorkDay(): FormGroup {
    return this.fb.group({
      validFrom: [''],
      day: ['', Validators.required],
      from: ['', Validators.required],
      until: ['', Validators.required]
    });
  }

  addDiscipline() {
    this.disciplines.push(this.fb.control(''));
  }

  addWorkDay() {
    this.workDays.push(this.createWorkDay());
  }

  removeDiscipline(index: number) {
    this.disciplines.removeAt(index);
  }

  removeWorkDay(index: number) {
    this.workDays.removeAt(index);
  }

  addImage() {
    this.images.push(this.fb.control(''));
  }

  removeImage(index: number) {
    this.images.removeAt(index);
  }

  getAvailableDays(index: number): string[] {
    const selectedDays = this.workDays.controls
      .filter((_, i) => i !== index)
      .map(control => control.get('day')?.value)
      .filter(value => value);  // Filter out empty values
    return this.allDays.filter(day => !selectedDays.includes(day));
  }

  formatTime(time: string): string {
    const [hours, minutes] = time.split(':');
    return `${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}:00`;
  }

  onFileSelect(event: Event, index: number) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.images.at(index).setValue(file);
    }
  }

  onSubmit() {
    if (this.facilityForm.valid) {
      const formValue = this.facilityForm.value;
  
      if (formValue.images.length === 1) {
        alert('Please upload at least two images or none.');
        return;
      }
  
      const formData = new FormData();
      formData.append('facility', new Blob([JSON.stringify({
        name: formValue.name,
        description: formValue.description,
        address: formValue.address,
        city: formValue.city,
        disciplines: formValue.disciplines,
        workDays: formValue.workDays.map((workDay: any) => ({
          ...workDay,
          validFrom: workDay.validFrom || new Date().toISOString().split('T')[0],
          from: this.formatTime(workDay.from),
          until: this.formatTime(workDay.until)
        }))
      })], { type: "application/json" }));
  
      formValue.images.forEach((image: File) => {
        formData.append('images', image);
      });
  
      this.facilityService.createFacilityWithImages(formData).subscribe(
        (facility: any) => {
          alert('Facility created successfully');
          this.router.navigate(['/facilities']);
        },
        (error) => {
          console.error('Error creating facility', error);
          if (error.status === 413) {
            alert('File size exceeds the maximum limit!');
          } else {
            alert('Error creating facility');
          }
        }
      );
    }
  }  
}
