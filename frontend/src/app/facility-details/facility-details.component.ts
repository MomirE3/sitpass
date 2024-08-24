import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FacilityService } from '../services/facility.service';
import { Facility } from '../services/facility.model';
import { ReviewDTO, ReviewService } from '../services/review.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-facility-details',
  templateUrl: './facility-details.component.html',
  styleUrls: ['./facility-details.component.scss']
})
export class FacilityDetailsComponent implements OnInit {
  facility: Facility | undefined;
  reviews: ReviewDTO[] = [];
  isManager: boolean = false;
  private daysOfWeekOrder = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  constructor(
    private route: ActivatedRoute,
    private facilityService: FacilityService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.facilityService.getFacility(+id).subscribe(
        (facility) => {
          this.facility = facility;
          this.checkIfManagerOrAdmin(facility.id);
          this.fetchReviewsSortedByDate(true); 
        },
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

  fetchReviewsSortedByRating(ascending: boolean): void {
    if (this.facility) {
        this.reviewService.getReviewsSortedByRating(this.facility.id, ascending).subscribe(
            (reviews) => this.reviews = reviews.map(review => this.convertReviewDate(review)),
            (error) => console.error('Error fetching reviews', error)
        );
    }
}

fetchReviewsSortedByDate(ascending: boolean): void {
    if (this.facility) {
        this.reviewService.getReviewsSortedByDate(this.facility.id, ascending).subscribe(
            (reviews) => this.reviews = reviews.map(review => this.convertReviewDate(review)),
            (error) => console.error('Error fetching reviews', error)
        );
    }
}


  checkIfManagerOrAdmin(facilityId: number): void {
    if (this.authService.isAdmin()) {
      this.isManager = true;
    } else {
      this.facilityService.isManagerOfFacility(facilityId).subscribe(
        (isManager: boolean) => this.isManager = isManager,
        (error: any) => {
          if (error.status === 401) {
            console.error('Unauthorized, redirecting to login...');
            this.router.navigate(['/login']);
          } else {
            console.error('Error checking if user is manager', error);
          }
        }
      );
    }
  }

  navigateToUpdate(): void {
    if (this.facility) {
      this.router.navigate([`/facilities/${this.facility.id}/update`]);
    }
  }

  formatTime(time: string | number[]): string {
    if (typeof time === 'string') {
      return time;
    } else if (Array.isArray(time)) {
      const [hours, minutes] = time;
      return `${this.padTime(hours)}:${this.padTime(minutes)}`;
    }
    return '';
  }
  
  padTime(value: number): string {
    return value < 10 ? `0${value}` : `${value}`;
  }

  sortedWorkDays() {
    return this.facility?.workDays.sort((a, b) => this.daysOfWeekOrder.indexOf(a.day) - this.daysOfWeekOrder.indexOf(b.day));
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
navigateToAnalytics(): void {
  if (this.facility) {
    this.router.navigate([`/facilities/${this.facility.id}/analytics`]);
  }
}

}
