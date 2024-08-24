package svt.projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Manages;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ManagesRepository extends JpaRepository<Manages, Long> {

    @Query("SELECT m FROM Manages m WHERE m.facility.id = :facilityId AND m.endDate >= CURRENT_DATE")
    Optional<Manages> findByFacilityId(Long facilityId);
    boolean existsByFacilityIdAndUserId(Long facilityId, Long userId);
    boolean existsByFacilityIdAndStartDateBeforeAndEndDateAfter(Long facilityId, LocalDate endDate, LocalDate startDate);
    boolean existsByUserId(Long userId);

}
