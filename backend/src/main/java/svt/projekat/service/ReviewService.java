package svt.projekat.service;

import svt.projekat.model.dto.CommentDTO;
import svt.projekat.model.dto.ReviewDTO;
import svt.projekat.model.entity.Comment;
import svt.projekat.model.entity.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewDTO reviewDTO, long userId);

    void deleteReview(Long reviewId);

    void hideReview(Long reviewId);

    List<ReviewDTO> findAllReviewsForFacility(Long facilityId);
    Review findReviewById(Long id);
    void showReview(Long reviewId);
    Comment replyToComment(CommentDTO commentDTO, long userId);
    List<ReviewDTO> findReviewsByUserId(long userId);
    List<ReviewDTO> getReviewsSortedByRating(Long facilityId, boolean ascending);
    List<ReviewDTO> getReviewsSortedByDate(Long facilityId, boolean ascending);

}