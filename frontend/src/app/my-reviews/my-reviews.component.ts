import { Component, OnInit } from '@angular/core';
import { ReviewService, ReviewDTO } from '../services/review.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-my-reviews',
  templateUrl: './my-reviews.component.html',
  styleUrls: ['./my-reviews.component.scss']
})
export class MyReviewsComponent implements OnInit {
  reviews: ReviewDTO[] = [];

  constructor(private reviewService: ReviewService, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadMyReviews();
  }

  loadMyReviews(): void {
    this.reviewService.getUserReviews().subscribe((data: ReviewDTO[]) => {
      console.log('Loaded User Reviews:', data);
      this.reviews = data;
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
      this.loadMyReviews();
    });
  }
}
