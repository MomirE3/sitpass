import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommentDTO } from '../services/review.service';

@Component({
  selector: 'app-comment-item',
  templateUrl: './comment-item.component.html',
  styleUrls: ['./comment-item.component.scss']
})
export class CommentItemComponent {
  @Input() comment!: CommentDTO;
  @Output() replyClicked = new EventEmitter<number>();

  openReplyDialog(): void {
    this.replyClicked.emit(this.comment.id);
  }
}
