import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FacilityService } from '../services/facility.service';
import { Facility } from '../services/facility.model';
import { ExerciseService, Exercise } from '../services/exercise.service';
import { AuthService } from '../services/auth.service';

declare var bootstrap: any;

type FilterKey = 'cities' | 'disciplines';

@Component({
  selector: 'app-facilities',
  templateUrl: './facilities.component.html',
  styleUrls: ['./facilities.component.scss']
})
export class FacilitiesComponent implements OnInit {
  facilitiesInCity: Facility[] = [];
  mostPopularFacilities: Facility[] = [];
  visitedFacilities: Facility[] = [];
  newFacilities: Facility[] = [];
  filteredFacilities: Facility[] = [];
  cities: string[] = [];
  disciplines: string[] = [];
  appointmentForm: FormGroup;
  selectedFacilityId: number | null = null;
  isUserOrAdmin: boolean = false;

  // Pagination control for "Explore More"
  currentPage: number = 0;
  pageSize: number = 5;
  isMoreAvailable: boolean = true;

  // Flag to check if filters are applied
  isFiltered: boolean = false;

  filters = {
    cities: [] as string[],
    minRating: null as number | null,
    maxRating: null as number | null,
    fromTime: '',
    untilTime: '',
    disciplines: [] as string[]
  };

  constructor(
    private facilityService: FacilityService,
    private fb: FormBuilder,
    private exerciseService: ExerciseService,
    private authService: AuthService
  ) {
    this.appointmentForm = this.fb.group({
      from: ['', Validators.required],
      until: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadHomepageData();
    this.getUniqueDisciplineNames();
    this.getUniqueCityNames();
    this.checkUserType();
  }

  loadHomepageData(): void {
    this.facilityService.getHomepageData().subscribe(
      (data) => {
        console.log('Homepage data:', data);
        this.facilitiesInCity = data.facilitiesInCity;
        this.mostPopularFacilities = data.mostPopularFacilities;
        this.visitedFacilities = data.visitedFacilities;
        this.newFacilities = data.newFacilities;
      },
      (error) => {
        console.error('Error loading homepage data', error);
      }
    );
  }

  getUniqueDisciplineNames(): void {
    this.facilityService.getDisciplines().subscribe(
      data => {
        console.log('Discipline names:', data);
        this.disciplines = data;
      },
      error => {
        console.error('Error fetching discipline names', error);
      }
    );
  }

  getUniqueCityNames(): void {
    this.facilityService.getUniqueCities().subscribe(
      data => {
        console.log('City names:', data);
        this.cities = data;
      },
      error => {
        console.error('Error fetching city names', error);
      }
    );
  }

  applyFilters(): void {
    const cities = this.filters.cities.length > 0 ? this.filters.cities : undefined;
    const disciplines = this.filters.disciplines.length > 0 ? this.filters.disciplines : undefined;

    console.log(this.filters);
    this.facilityService.filterFacilities(
      disciplines,
      this.filters.minRating,
      this.filters.maxRating,
      this.filters.fromTime,
      this.filters.untilTime,
      cities
    ).subscribe(
      data => {
        console.log('Filtered Facilities data:', data);
        this.filteredFacilities = data;
        this.isFiltered = true;
        this.isMoreAvailable = false; // Disable "Explore More" after filtering
      },
      error => {
        console.error('Error fetching filtered facilities', error);
      }
    );
  }

  clearFilters(): void {
    this.isFiltered = false;
    this.filteredFacilities = [];
    this.loadHomepageData();
  }

  onCheckboxChange(event: any, type: FilterKey): void {
    const value = event.target.value;
    if (event.target.checked) {
      this.filters[type].push(value);
    } else {
      const index = this.filters[type].indexOf(value);
      if (index > -1) {
        this.filters[type].splice(index, 1);
      }
    }
  }

  checkUserType(): void {
    const userType = this.authService.getUserType();
    this.isUserOrAdmin = userType.toLowerCase() === 'user' || userType.toLowerCase() === 'administrator';
  }

  openModal(facilityId: number): void {
    this.selectedFacilityId = facilityId;
    const modal = new bootstrap.Modal(document.getElementById(`appointmentModal${facilityId}`));
    modal.show();
  }

  onSubmit(): void {
    const userId = this.authService.getUserId();
    console.log('User ID on submit:', userId);

    if (this.appointmentForm.valid && this.selectedFacilityId !== null && userId !== undefined) {
      const exercise: Exercise = {
        userId: userId,
        facilityId: this.selectedFacilityId,
        from: this.appointmentForm.value.from,
        until: this.appointmentForm.value.until
      };

      this.exerciseService.createExercise(exercise).subscribe(
        response => {
          console.log('Response from server:', response);
          alert(response);  
          this.resetForm();
          this.closeModal();
        },
        error => {
          console.error('Error from server:', error);
          alert(error.error);  
        }
      );
    } else {
      console.error('Form is invalid, selectedFacilityId is null, or userId is undefined');
    }
  }

  private resetForm(): void {
    this.appointmentForm.reset();
    this.selectedFacilityId = null;
  }

  private closeModal(): void {
    const modalElement = document.querySelector('.modal.show'); 
    if (modalElement) {
      console.log('Modal element found:', modalElement);
      const modalInstance = bootstrap.Modal.getInstance(modalElement);
      if (modalInstance) {
        console.log('Closing modal instance:', modalInstance);
        modalInstance.hide();
      } else {
        console.error('No modal instance found for element:', modalElement);
      }
    } else {
      console.error('No modal element found to close');
    }
  }

  exploreMore(): void {
    if (this.isMoreAvailable) {
      this.loadNewFacilities();
    }
  }

  private loadNewFacilities(): void {
    this.facilityService.getNewFacilities(this.currentPage, this.pageSize).subscribe(
      (data) => {
        console.log('New Facilities data:', data.newFacilities);
        if (data.newFacilities.length < this.pageSize) {
          this.isMoreAvailable = false; // No more facilities available
        }
        this.newFacilities.push(...data.newFacilities);
        this.currentPage++;
      },
      (error) => {
        console.error('Error loading new facilities', error);
      }
    );
  }
}
