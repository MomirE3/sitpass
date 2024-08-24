import { Component, OnInit } from '@angular/core';
import { ReviewService, FacilityDTO, ReviewDTO, CommentDTO } from '../services/review.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss']
})
export class ReviewsComponent implements OnInit {
  facilities: FacilityDTO[] = [];

  constructor(private reviewService: ReviewService, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.authService.isManagerOrAdmin().subscribe((isManagerOrAdmin) => {
      if (isManagerOrAdmin) {
        this.reviewService.getAllReviews().subscribe((data: FacilityDTO[]) => {
          console.log('Loaded Reviews:', data);
          this.facilities = data;
        });
      } else {
        console.error('User is not authorized to view this page');
      }
    });
  }

  toggleReviewVisibility(reviewId: number, isHidden: boolean): void {
    if (isHidden) {
      this.reviewService.showReview(reviewId).subscribe(() => {
        this.loadReviews();
      });
    } else {
      this.reviewService.hideReview(reviewId).subscribe(() => {
        this.loadReviews();
      });
    }
  }

  deleteReview(reviewId: number): void {
    this.reviewService.deleteReview(reviewId).subscribe(() => {
      this.loadReviews();
    });
  }

  openReplyDialog(commentId: number): void {
    const replyText = prompt('Enter your reply:');
    if (replyText) {
      this.replyToComment(commentId, replyText);
    }
  }

  replyToComment(parentCommentId: number, text: string): void {
    const commentDTO = { text, parentCommentId };
    this.reviewService.replyToComment(commentDTO).subscribe(() => {
      this.loadReviews();
    });
  }
}
