import { Pipe, PipeTransform } from '@angular/core';
import { CommentDTO } from '../services/review.service';

@Pipe({
  name: 'filterByParent'
})
export class FilterByParentPipe implements PipeTransform {
  transform(comments: CommentDTO[], parentCommentId: number): CommentDTO[] {
    return comments.filter(comment => comment.parentCommentId === parentCommentId);
  }
}
