import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewService, ReviewDTO } from '../services/review.service';
import { FacilityService } from '../services/facility.service';
import { Facility } from '../services/facility.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-add-review',
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.scss']
})
export class AddReviewComponent implements OnInit {
  reviewForm: FormGroup;
  facilities: Facility[] = [];
  successMessage: string = '';
  errorMessage: string = '';
  numberOfVisits: number | null = null;
  isAdmin: boolean = false;

  constructor(
    private fb: FormBuilder,
    private reviewService: ReviewService,
    private facilityService: FacilityService,
    private authService: AuthService
  ) {
    this.reviewForm = this.fb.group({
      facilityId: ['', Validators.required],
      equipmentRating: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
      staffRating: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
      hygieneRating: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
      spaceRating: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
      comment: ['']
    });
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadEligibleFacilities();
  }

  loadEligibleFacilities(): void {
    this.facilityService.getEligibleFacilitiesForReview().subscribe(
      data => {
        this.facilities = data;
      },
      error => {
        console.error('Error fetching eligible facilities', error);
      }
    );
  }

  onFacilityChange(): void {
    const facilityId = this.reviewForm.get('facilityId')?.value;
    if (facilityId) {
      this.facilityService.getNumberOfVisits(facilityId).subscribe(
        data => {
          this.numberOfVisits = data;
        },
        error => {
          console.error('Error fetching number of visits', error);
        }
      );
    } else {
      this.numberOfVisits = null;
    }
  }

  onSubmit(): void {
    if (this.reviewForm.valid) {
      const reviewDTO: ReviewDTO = this.reviewForm.value;
      this.reviewService.createReview(reviewDTO).subscribe(
        response => {
          this.successMessage = response.message;
          this.errorMessage = '';
          this.reviewForm.reset();
          this.numberOfVisits = null;
          this.loadEligibleFacilities();
        },
        error => {
          this.successMessage = '';
          this.errorMessage = error.error.error;
        }
      );
    }
  }
}
