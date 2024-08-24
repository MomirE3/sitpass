package svt.projekat.service;

import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.FacilityCreationDTO;
import svt.projekat.model.dto.FacilityDTO;
import svt.projekat.model.entity.Facility;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public interface FacilityService {
    List<FacilityDTO> findAll();

    FacilityDTO findById(Long id);

    FacilityDTO createFacilityWithImages(FacilityCreationDTO request, List<MultipartFile> images) throws IOException;
    List<Facility> filterFacilities(List<String> disciplineNames, Double minRating, Double maxRating,
                                    LocalTime fromTime, LocalTime untilTime, List<String> cities);

    List<String> getAllUniqueCities();

    List<FacilityDTO> findEligibleFacilitiesForReview(Long userId);

    List<Facility> getActiveFacilities();

    List<Facility> getInactiveFacilities();

    List<Facility> findNewFacilitiesForUser(Long userId);
    List<Facility> findFacilitiesUserHasVisited(Long userId);
    List<Facility> findMostPopularFacilities();
    List<Facility> findFacilitiesInUserCity(Long userId);
    List<Facility> findNewFacilitiesForUser(Long userId, int page, int size);

    FacilityDTO updateFacility(Long id, FacilityDTO facilityDTO);
    FacilityDTO updateFacilityImages(Long id, List<MultipartFile> images, List<String> imagesToDelete) throws IOException;
    boolean isManagerOfFacility(Long facilityId, Long userId);

    List<FacilityDTO> findFacilitiesManagedByUser(Long userId);

    boolean isManagerOfAnyFacility(Long userId);

    boolean isAdmin(Long userId);
}
