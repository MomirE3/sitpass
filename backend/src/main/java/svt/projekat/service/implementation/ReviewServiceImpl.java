package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.model.dto.CommentDTO;
import svt.projekat.model.dto.ReviewDTO;
import svt.projekat.model.entity.*;
import svt.projekat.repository.*;
import svt.projekat.service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ManagesRepository managesRepository;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Review createReview(ReviewDTO reviewDTO, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Facility facility = facilityRepository.findById(reviewDTO.getFacilityId()).orElseThrow(() -> new IllegalArgumentException("Invalid facility ID"));
        if (!facility.isActive()) {
            throw new IllegalArgumentException("Cannot create review for an inactive facility");
        }

        Optional<Review> existingReview = reviewRepository.findByUserAndFacility(user, facility);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("User has already added a review for this facility");
        }

        boolean isAdmin = user instanceof Administrator;

        if (!isAdmin) {
            LocalDateTime currentDate = LocalDateTime.now();
            List<Exercise> exercises = exerciseRepository.findByUserIdAndFacilityIdAndUntilDateTimeBefore(userId, reviewDTO.getFacilityId(), currentDate);
            if (exercises.isEmpty()) {
                throw new IllegalArgumentException("User has not visited this facility or the visit is not yet completed");
            }
        }

        validateRating(reviewDTO.getEquipmentRating(), "Equipment");
        validateRating(reviewDTO.getStaffRating(), "Staff");
        validateRating(reviewDTO.getHygieneRating(), "Hygiene");
        validateRating(reviewDTO.getSpaceRating(), "Space");

        Rate rate = new Rate();
        rate.setEquipment(reviewDTO.getEquipmentRating());
        rate.setStaff(reviewDTO.getStaffRating());
        rate.setHygiene(reviewDTO.getHygieneRating());
        rate.setSpace(reviewDTO.getSpaceRating());
        rateRepository.save(rate);

        Review review = new Review();
        review.setUser(user);
        review.setFacility(facility);
        review.setRate(rate);
        review.setCreatedAt(LocalDateTime.now());
        review.setExercisesCount(isAdmin ? 0 : exerciseRepository.countByUserIdAndFacilityIdAndUntilDateTimeBefore(userId, reviewDTO.getFacilityId(), LocalDateTime.now()));
        review.setHidden(false);
        review.setDeleted(false);

        review = reviewRepository.save(review);

        if (reviewDTO.getComment() != null && !reviewDTO.getComment().isEmpty()) {
            Comment comment = new Comment();
            comment.setText(reviewDTO.getComment());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUser(user);
            comment.setReview(review);
            commentRepository.save(comment);
            review.getComments().add(comment);
        }

        updateFacilityTotalRating(facility.getId());

        return review;
    }

    private void validateRating(Integer rating, String ratingType) {
        if (rating == null || rating < 1 || rating > 10) {
            throw new IllegalArgumentException(ratingType + " rating must be between 1 and 10");
        }
    }

    private void updateFacilityTotalRating(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new IllegalArgumentException("Invalid facility ID"));

        List<Review> reviews = reviewRepository.findByFacilityIdAndDeletedFalse(facilityId);
        List<Review> visibleReviews = reviews.stream()
                .filter(review -> !review.getHidden())
                .collect(Collectors.toList());

        if (visibleReviews.isEmpty()) {
            facility.setTotalRating(0.0);
        } else {
            double totalRating = visibleReviews.stream()
                    .mapToDouble(review -> {
                        Rate rate = review.getRate();
                        return (rate.getEquipment() + rate.getHygiene() + rate.getSpace() + rate.getStaff()) / 4.0;
                    })
                    .average()
                    .orElse(0.0);
            facility.setTotalRating(totalRating);
        }

        facilityRepository.save(facility);
    }

    @Override
    public List<ReviewDTO> findReviewsByUserId(long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setId(review.getId());
                    dto.setCreatedAt(review.getCreatedAt());
                    dto.setExercisesCount(review.getExercisesCount());
                    dto.setHidden(review.getHidden());
                    dto.setFacilityId(review.getFacility().getId());
                    dto.setRateId(review.getRate().getId());
                    dto.setUserId(review.getUser().getId());
                    dto.setEquipmentRating(review.getRate().getEquipment());
                    dto.setStaffRating(review.getRate().getStaff());
                    dto.setHygieneRating(review.getRate().getHygiene());
                    dto.setSpaceRating(review.getRate().getSpace());
                    dto.setComment(review.getComments().stream().findFirst().map(Comment::getText).orElse(null));
                    dto.setEmail(review.getUser().getEmail());
                    dto.setComments(review.getComments().stream().map(this::mapToCommentDTO).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void hideReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Invalid review ID"));
        review.setHidden(true);
        reviewRepository.save(review);
    }

    @Override
    public void showReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Invalid review ID"));
        review.setHidden(false);
        reviewRepository.save(review);
    }


    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Invalid review ID"));
        review.setDeleted(true);
        reviewRepository.save(review);
        updateFacilityTotalRating(review.getFacility().getId());
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : 0);
        dto.setReviewId(comment.getReview().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setEmail(comment.getUser().getEmail());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setReplies(comment.getReplies().stream().map(this::mapToCommentDTO).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<ReviewDTO> findAllReviewsForFacility(Long facilityId) {
        List<Review> reviews = reviewRepository.findByFacilityIdAndDeletedFalse(facilityId);
        return reviews.stream()
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setId(review.getId());
                    dto.setCreatedAt(review.getCreatedAt());
                    dto.setExercisesCount(review.getExercisesCount());
                    dto.setHidden(review.getHidden());
                    dto.setFacilityId(review.getFacility().getId());
                    dto.setRateId(review.getRate().getId());
                    dto.setUserId(review.getUser().getId());
                    dto.setEquipmentRating(review.getRate().getEquipment());
                    dto.setStaffRating(review.getRate().getStaff());
                    dto.setHygieneRating(review.getRate().getHygiene());
                    dto.setSpaceRating(review.getRate().getSpace());
                    dto.setComment(review.getComments().stream().findFirst().map(Comment::getText).orElse(null));
                    dto.setEmail(review.getUser().getEmail());
                    dto.setComments(review.getComments().stream().map(this::mapToCommentDTO).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Review findReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Review not found"));
    }

    @Override
    public Comment replyToComment(CommentDTO commentDTO, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Comment parentComment = commentRepository.findById(commentDTO.getParentCommentId()).orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID"));

        if (!canUserReply(user, parentComment)) {
            throw new IllegalArgumentException("User is not allowed to reply to this comment");
        }

        Comment reply = new Comment();
        reply.setText(commentDTO.getText());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(user);
        reply.setParentComment(parentComment);
        reply.setReview(parentComment.getReview());

        return commentRepository.save(reply);
    }

    private boolean canUserReply(User user, Comment parentComment) {
        if (user instanceof Administrator) {
            return true;
        }
        Review review = parentComment.getReview();
        Facility facility = review.getFacility();
        boolean isManager = managesRepository.existsByFacilityIdAndUserId(facility.getId(), user.getId());
        if (isManager) {
            return true;
        }
        if (parentComment.getParentComment() != null) {
            return parentComment.getParentComment().getUser().getId() == user.getId();
        }
        return false;
    }

    @Override
    public List<ReviewDTO> getReviewsSortedByRating(Long facilityId, boolean ascending) {
        List<Review> reviews = ascending ?
                reviewRepository.findByFacilityIdOrderByRatingAsc(facilityId) :
                reviewRepository.findByFacilityIdOrderByRatingDesc(facilityId);

        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsSortedByDate(Long facilityId, boolean ascending) {
        List<Review> reviews = ascending ?
                reviewRepository.findByFacilityIdOrderByCreatedAtAsc(facilityId) :
                reviewRepository.findByFacilityIdOrderByCreatedAtDesc(facilityId);

        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setFacilityId(review.getFacility().getId());
        dto.setFacilityName(review.getFacility().getName());
        dto.setEquipmentRating(review.getRate().getEquipment());
        dto.setStaffRating(review.getRate().getStaff());
        dto.setHygieneRating(review.getRate().getHygiene());
        dto.setSpaceRating(review.getRate().getSpace());
        dto.setComment(review.getComments().stream().findFirst().map(Comment::getText).orElse(null));
        dto.setHidden(review.getHidden());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setExercisesCount(review.getExercisesCount());
        dto.setRateId(review.getRate().getId());
        dto.setUserId(review.getUser().getId());
        dto.setEmail(review.getUser().getEmail());
        dto.setComments(review.getComments().stream().map(this::mapToCommentDTO).collect(Collectors.toList()));
        return dto;
    }

}
