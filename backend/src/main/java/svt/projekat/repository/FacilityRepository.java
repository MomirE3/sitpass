package svt.projekat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long>, FacilityRepositoryCustom {
    @Query("SELECT DISTINCT f.city FROM Facility f WHERE f.active = true")
    List<String> findAllUniqueCities();
    @Query("SELECT f FROM Facility f WHERE f.active = false")
    List<Facility> findByActiveFalse();
    @Query("SELECT f FROM Facility f WHERE f.active = true")
    List<Facility> findByActiveTrue();
    @Query("SELECT f FROM Facility f JOIN f.manages m JOIN m.user u WHERE u = :manager")
    List<Facility> findByManages_User(@Param("manager") User manager);

    @Query("SELECT f FROM Facility f WHERE f.id IN (SELECT m.facility.id FROM Manages m WHERE m.user.id = :userId)")
    List<Facility> findByManagerId(@Param("userId") Long userId);

    @Query("SELECT f FROM Facility f WHERE f.active = true AND "
            + "(:cities IS NULL OR f.city IN :cities) AND "
            + "(:disciplines IS NULL OR EXISTS (SELECT d FROM Discipline d WHERE d.byFacility = f AND d.name IN :disciplines)) AND "
            + "(:minRating IS NULL OR f.totalRating >= :minRating) AND "
            + "(:maxRating IS NULL OR f.totalRating <= :maxRating) AND "
            + "(:fromTime IS NULL OR EXISTS (SELECT w FROM WorkDay w WHERE w.facility = f AND w.from <= :fromTime AND w.until >= :untilTime))")
    List<Facility> filterFacilities(@Param("cities") List<String> cities,
                                    @Param("disciplines") List<String> disciplines,
                                    @Param("minRating") Double minRating,
                                    @Param("maxRating") Double maxRating,
                                    @Param("fromTime") LocalTime fromTime,
                                    @Param("untilTime") LocalTime untilTime);

    List<Facility> findByCity(String city);

    @Query("SELECT f FROM Facility f " +
            "WHERE f.active = true " +
            "AND f.id NOT IN (" +
            "    SELECT e.atFacility.id FROM Exercise e WHERE e.wentBy.id = :userId" +
            ") " +
            "AND f.id NOT IN (" +
            "    SELECT DISTINCT d.byFacility.id FROM Discipline d " +
            "    WHERE d.byFacility.id IN (" +
            "        SELECT e.atFacility.id FROM Exercise e WHERE e.wentBy.id = :userId" +
            "    )" +
            ")")
    Page<Facility> findNewFacilitiesForUser(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT f FROM Facility f WHERE f.active = true ORDER BY f.totalRating DESC LIMIT 5")
    List<Facility> findTop5ByOrderByTotalRatingDesc();

    @Query("SELECT f FROM Facility f " +
            "WHERE f.city = :city " +
            "AND f.id NOT IN (" +
            "SELECT e.atFacility.id FROM Exercise e WHERE e.wentBy.id = :userId" +
            ") " +
            "AND f.id NOT IN (" +
            "SELECT DISTINCT d.byFacility.id FROM Discipline d " +
            "WHERE d.byFacility.id IN (" +
            "SELECT e.atFacility.id FROM Exercise e WHERE e.wentBy.id = :userId" +
            ") " +
            ")")
    List<Facility> findNewFacilitiesForUser(@Param("userId") Long userId, @Param("city") String city);

    @Query("SELECT COUNT(e) FROM Exercise e WHERE e.atFacility.id = :facilityId AND e.from >= :startDate AND e.until <= :endDate")
    long countUsersByFacilityAndDateRange(@Param("facilityId") Long facilityId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.facility.id = :facilityId AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
    long countReviewsByFacilityAndDateRange(@Param("facilityId") Long facilityId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT HOUR(e.from), COUNT(e) FROM Exercise e WHERE e.atFacility.id = :facilityId AND e.from >= :startDate AND e.until <= :endDate GROUP BY HOUR(e.from)")
    List<Object[]> findUserActivityByHour(@Param("facilityId") Long facilityId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(f) > 0 FROM Facility f JOIN f.manages m WHERE f.id = :facilityId AND m.user.id = :userId")
    boolean existsByIdAndManagerId(@Param("facilityId") Long facilityId, @Param("userId") Long userId);

}
