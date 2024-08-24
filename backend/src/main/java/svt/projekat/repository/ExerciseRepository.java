package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Exercise;
import svt.projekat.model.entity.Facility;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByWentById(Long userId);

    @Query("SELECT e FROM Exercise e WHERE e.wentBy.id = :userId AND e.atFacility.id = :facilityId AND e.until <= :currentDate")
    List<Exercise> findByUserIdAndFacilityIdAndUntilDateTimeBefore(@Param("userId") long userId, @Param("facilityId") long facilityId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT DISTINCT e.atFacility FROM Exercise e WHERE e.wentBy.id = :userId AND e.until < :currentDateTime")
    List<Facility> findDistinctFacilitiesByUserIdAndUntilDateTimeBefore(Long userId, LocalDateTime currentDateTime);

    @Query("SELECT COUNT(e) FROM Exercise e WHERE e.wentBy.id = :userId AND e.atFacility.id = :facilityId AND e.until <= :currentDate")
    int countByUserIdAndFacilityIdAndUntilDateTimeBefore(@Param("userId") Long userId, @Param("facilityId") Long facilityId, @Param("currentDate") LocalDateTime currentDate);
}
