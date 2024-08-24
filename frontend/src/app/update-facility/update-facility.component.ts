import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { FacilityService } from '../services/facility.service';
import { Discipline, Facility, WorkDay } from '../services/facility.model';
import { HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-update-facility',
  templateUrl: './update-facility.component.html',
  styleUrls: ['./update-facility.component.scss']
})
export class UpdateFacilityComponent implements OnInit {
  facilityForm: FormGroup;
  facilityId: number | null = null;
  allDays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  imagesToDelete: string[] = [];
  imagesToStay: string[] = [];
  newImages: { file: File, preview: string | ArrayBuffer | null }[] = [];

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private facilityService: FacilityService,
    private router: Router
  ) {
    this.facilityForm = this.fb.group({
      name: ['', Validators.required],
      city: ['', Validators.required],
      address: ['', Validators.required],
      description: ['', Validators.required],
      disciplines: this.fb.array([]),
      workDays: this.fb.array([]),
      images: this.fb.array([])
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.facilityId = +id;
      this.facilityService.getFacility(this.facilityId).subscribe(
        (facility) => this.populateForm(facility),
        (error) => {
          if (error.status === 401) {
            console.error('Unauthorized, redirecting to login...');
            this.router.navigate(['/login']);
          } else {
            console.error('Error fetching facility', error);
          }
        }
      );
    }
  }

  get disciplines(): FormArray {
    return this.facilityForm.get('disciplines') as FormArray;
  }

  get workDays(): FormArray {
    return this.facilityForm.get('workDays') as FormArray;
  }

  get images(): FormArray {
    return this.facilityForm.get('images') as FormArray;
  }

  populateForm(facility: Facility): void {
    this.facilityForm.patchValue({
      name: facility.name,
      city: facility.city,
      address: facility.address,
      description: facility.description,
    });

    this.disciplines.clear();
    facility.disciplines.forEach(discipline => {
      this.disciplines.push(this.fb.group({
        name: [discipline, Validators.required]
      }));
    });

    this.workDays.clear();
    facility.workDays.forEach(workDay => {
      this.workDays.push(this.fb.group({
        id: [workDay.id],
        validFrom: [workDay.validFrom ? new Date(workDay.validFrom).toISOString().split('T')[0] : '', Validators.required],
        day: [workDay.day, Validators.required],
        from: [workDay.from, Validators.required],
        until: [workDay.until, Validators.required]
      }));
    });

    this.images.clear();
    if (facility.images) {
      facility.images.forEach(imagePath => {
        this.images.push(this.fb.group({
          path: [imagePath]
        }));
        this.imagesToStay.push(imagePath);
      });
    }
  }

  addDiscipline(): void {
    const disciplineControl = this.fb.group({
      name: ['', Validators.required]
    });
    this.disciplines.push(disciplineControl);
  }

  addWorkDay(): void {
    const workDayControl = this.fb.group({
      id: [null],
      validFrom: [new Date().toISOString().split('T')[0], Validators.required],
      day: ['', Validators.required],
      from: ['', Validators.required],
      until: ['', Validators.required]
    });
    this.workDays.push(workDayControl);
  }

  removeDiscipline(index: number): void {
    this.disciplines.removeAt(index);
  }

  removeWorkDay(index: number): void {
    this.workDays.removeAt(index);
  }

  removeImage(index: number): void {
    const imageControl = this.images.at(index);
    const imagePath = imageControl.get('path')?.value;
    if (imagePath) {
      this.imagesToDelete.push(imagePath);
      this.imagesToStay = this.imagesToStay.filter(path => path !== imagePath);
    }
    this.images.removeAt(index);

    // Remove from newImages if it is there
    const newImageIndex = this.newImages.findIndex(img => img.preview === imagePath);
    if (newImageIndex > -1) {
      this.newImages.splice(newImageIndex, 1);
    }
  }

  addImage(): void {
    const imageControl = this.fb.group({
      path: [null]
    });
    this.images.push(imageControl);
  }

  getAvailableDays(index: number): string[] {
    const selectedDays = this.workDays.controls
      .filter((_, i) => i !== index)
      .map(control => control.get('day')?.value)
      .filter(value => value);
    return this.allDays.filter(day => !selectedDays.includes(day));
  }

  onFileSelect(event: any, index: number): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result as string | ArrayBuffer | null;
        this.newImages.push({ file, preview: result });
        const imageControl = this.images.at(index);
        imageControl.get('path')?.setValue(result);
      };
      reader.readAsDataURL(file);
    }
  }

  onNewImageChange(event: any): void {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result as string | ArrayBuffer | null;
        this.newImages.push({ file, preview: result });
        this.addImage();
        const index = this.images.length - 1;
        const imageControl = this.images.at(index);
        imageControl.get('path')?.setValue(result);
      };
      reader.readAsDataURL(file);
    }
  }

  validateImages(): boolean {
    const totalImages = this.imagesToStay.length + this.newImages.length - this.imagesToDelete.length;
    return totalImages === 0 || totalImages > 1;
  }

  onSubmit(): void {
    if (!this.validateImages()) {
        alert('You must have either no images or at least two images.');
        return;
    }

    if (this.facilityForm.valid && this.facilityId !== null) {
        const formValue = this.facilityForm.value;
        const disciplines = formValue.disciplines.map((discipline: Discipline) => discipline.name);
        const workDays = formValue.workDays.map((workDay: WorkDay) => {
            const validFrom = new Date(workDay.validFrom).toISOString().split('T')[0];
            const from = workDay.from.length === 5 ? workDay.from + ':00' : workDay.from;
            const until = workDay.until.length === 5 ? workDay.until + ':00' : workDay.until;
            return { ...workDay, validFrom, from, until };
        });

        const updateData = { ...formValue, disciplines, workDays };
        delete updateData.images;

        const token = localStorage.getItem('token');
        const headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });

        this.facilityService.updateFacility(this.facilityId, updateData, { headers }).subscribe(
            (response) => {
                this.updateImages();
            },
            (error) => {
                if (error.status === 401) {
                    console.error('Unauthorized, redirecting to login...');
                } else {
                    console.error('Error updating facility', error);
                }
            }
        );
    } else {
        console.log('Form is invalid:', this.facilityForm);
    }
  }

  updateImages(): void {
    if (this.newImages.length > 0 || this.imagesToDelete.length > 0) {
        const formData = new FormData();
        this.newImages.forEach(item => formData.append('images', item.file));
        const imagesToDeleteRelativePaths = this.imagesToDelete.map(image => image.replace('http://localhost:8080', ''));
        formData.append('imagesToDelete', JSON.stringify(imagesToDeleteRelativePaths));

        const token = localStorage.getItem('token');
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });

        this.facilityService.updateFacilityImages(this.facilityId!, formData, { headers }).subscribe(
            (response) => {
                this.router.navigate([`/facilities/${this.facilityId}`]);
            },
            (error) => {
                if (error.status === 401) {
                    console.error('Unauthorized, redirecting to login...');
                } else {
                    console.error('Error updating images', error);
                }
            }
        );
    } else {
        this.router.navigate([`/facilities/${this.facilityId}`]);
    }
  }
}
