package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import svt.projekat.model.dto.CommentDTO;
import svt.projekat.model.dto.FacilityDTO;
import svt.projekat.model.dto.ReviewDTO;
import svt.projekat.model.entity.Review;
import svt.projekat.model.entity.User;
import svt.projekat.service.ReviewService;
import svt.projekat.service.UserService;
import svt.projekat.service.FacilityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private FacilityService facilityService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            reviewService.createReview(reviewDTO, user.getId());
            response.put("message", "Review created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/hide/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> hideReview(@PathVariable Long id, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            if (!facilityService.isManagerOfFacility(reviewService.findReviewById(id).getFacility().getId(), user.getId()) && !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            reviewService.hideReview(id);
            response.put("message", "Review hidden successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/show/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> showReview(@PathVariable Long id, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            if (!facilityService.isManagerOfFacility(reviewService.findReviewById(id).getFacility().getId(), user.getId()) && !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            reviewService.showReview(id);
            response.put("message", "Review shown successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long id, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            if (!facilityService.isManagerOfFacility(reviewService.findReviewById(id).getFacility().getId(), user.getId()) && !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            reviewService.deleteReview(id);
            response.put("message", "Review deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/facility/{facilityId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsForFacility(@PathVariable Long facilityId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        if (!facilityService.isManagerOfFacility(facilityId, user.getId()) && !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<ReviewDTO> reviews = reviewService.findAllReviewsForFacility(facilityId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FacilityDTO>> getAllFacilities(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<FacilityDTO> facilities;

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            facilities = facilityService.findAll();
        } else {
            facilities = facilityService.findFacilitiesManagedByUser(user.getId());
        }

        facilities.forEach(facility -> {
            List<ReviewDTO> reviews = reviewService.findAllReviewsForFacility(facility.getId());
            facility.setReviews(reviews);
        });

        return ResponseEntity.ok(facilities);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> replyToComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            reviewService.replyToComment(commentDTO, user.getId());
            response.put("message", "Reply added successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<ReviewDTO> reviews = reviewService.findReviewsByUserId(user.getId());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/facility/{facilityId}/sortByRating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsSortedByRating(
            @PathVariable Long facilityId,
            @RequestParam boolean ascending) {
        List<ReviewDTO> reviews = reviewService.getReviewsSortedByRating(facilityId, ascending);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/facility/{facilityId}/sortByDate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsSortedByDate(
            @PathVariable Long facilityId,
            @RequestParam boolean ascending) {
        List<ReviewDTO> reviews = reviewService.getReviewsSortedByDate(facilityId, ascending);
        return ResponseEntity.ok(reviews);
    }
}
