package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.Review;
import svt.projekat.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndFacility(User user, Facility facility);
    List<Review> findByUser(User user);
    List<Review> findByUserId(Long userId);
    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.deleted = false")
    List<Review> findByFacilityIdAndDeletedFalse(@Param("facilityId") Long facilityId);
    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.facility.active = true AND r.deleted = false ORDER BY r.rate.equipment + r.rate.staff + r.rate.hygiene + r.rate.space ASC")
    List<Review> findByFacilityIdOrderByRatingAsc(@Param("facilityId") Long facilityId);

    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.facility.active = true AND r.deleted = false ORDER BY r.rate.equipment + r.rate.staff + r.rate.hygiene + r.rate.space DESC")
    List<Review> findByFacilityIdOrderByRatingDesc(@Param("facilityId") Long facilityId);

    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.facility.active = true AND r.deleted = false ORDER BY r.createdAt ASC")
    List<Review> findByFacilityIdOrderByCreatedAtAsc(@Param("facilityId") Long facilityId);

    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.facility.active = true AND r.deleted = false ORDER BY r.createdAt DESC")
    List<Review> findByFacilityIdOrderByCreatedAtDesc(@Param("facilityId") Long facilityId);
}

