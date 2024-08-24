package svt.projekat.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.FacilityCreationDTO;
import svt.projekat.model.dto.FacilityDTO;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.User;
import svt.projekat.repository.FacilityRepository;
import svt.projekat.service.ExerciseService;
import svt.projekat.service.FacilityService;
import svt.projekat.service.UserService;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/facilities")
public class FacilityController {

    @Autowired
    FacilityService facilityService;

    @Autowired
    UserService userService;

    @Autowired
    ExerciseService exerciseService;

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @GetMapping
    public ResponseEntity<List<Facility>> getAllFacilities() {
        List<Facility> facilities = facilityService.getActiveFacilities();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDTO> getFacilityById(@PathVariable Long id) {
        FacilityDTO facility = facilityService.findById(id);
        if (facility != null) {
            return ResponseEntity.ok(facility);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createWithImages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFacilityWithImages(
            @RequestPart("facility") FacilityCreationDTO facilityCreationDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            if (images != null && images.size() == 1) {
                return new ResponseEntity<>("At least two images are required if any images are provided.", HttpStatus.BAD_REQUEST);
            }
            FacilityDTO facilityDTO = facilityService.createFacilityWithImages(facilityCreationDTO, images);
            return new ResponseEntity<>(facilityDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to save images", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Facility>> filter(@RequestParam(required = false) List<String> names,
                                                 @RequestParam(required = false) Double minRating,
                                                 @RequestParam(required = false) Double maxRating,
                                                 @RequestParam(required = false) String fromTime,
                                                 @RequestParam(required = false) String untilTime,
                                                 @RequestParam(required = false) List<String> cities) {
        LocalTime from = fromTime != null ? LocalTime.parse(fromTime) : null;
        LocalTime until = untilTime != null ? LocalTime.parse(untilTime) : null;

        List<Facility> facilities = facilityService.filterFacilities(names, minRating, maxRating, from, until, cities);

        if (facilities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(facilities, HttpStatus.OK);
    }


    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllUniqueCities() {
        List<String> cities = facilityService.getAllUniqueCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/eligibleForReview")
    public ResponseEntity<List<FacilityDTO>> getEligibleFacilitiesForReview(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        List<FacilityDTO> facilities = facilityService.findEligibleFacilitiesForReview(user.getId());
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/visits/{facilityId}")
    public ResponseEntity<Integer> getNumberOfVisits(@PathVariable Long facilityId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        int numberOfVisits = exerciseService.getNumberOfVisits(user.getId(), facilityId);
        return ResponseEntity.ok(numberOfVisits);
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Facility>> getInactiveFacilities() {
        List<Facility> inactiveFacilities = facilityService.getInactiveFacilities();
        return ResponseEntity.ok(inactiveFacilities);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Facility>> getActiveFacilities() {
        List<Facility> activeFacilities = facilityService.getActiveFacilities();
        return ResponseEntity.ok(activeFacilities);
    }

    @GetMapping("/homepage")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getFacilitiesForHomepage(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("facilitiesInCity", facilityService.findFacilitiesInUserCity(user.getId()));
        response.put("mostPopularFacilities", facilityService.findMostPopularFacilities());
        response.put("visitedFacilities", facilityService.findFacilitiesUserHasVisited(user.getId()));
        response.put("newFacilities", facilityService.findNewFacilitiesForUser(user.getId()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/newFacilities")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getNewFacilitiesForUser(Authentication authentication,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        List<Facility> newFacilities = facilityService.findNewFacilitiesForUser(user.getId(), page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("newFacilities", newFacilities);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/updateFacilityDetails")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateFacilityDetails(
            @PathVariable Long id,
            @RequestBody FacilityDTO request,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        System.out.println("Received payload: " + request);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = facilityService.isManagerOfFacility(id, user.getId());

        if (!isManager && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a manager of this facility.");
        }

        FacilityDTO updatedFacility = facilityService.updateFacility(id, request);
        if (updatedFacility != null) {
            return ResponseEntity.ok(updatedFacility);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/updateFacilityImages")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateFacilityImages(
            @PathVariable Long id,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "imagesToDelete", required = false) String imagesToDeleteJson,
            Authentication authentication, HttpServletRequest request) {

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = facilityService.isManagerOfFacility(id, user.getId());

        if (!isManager && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a manager of this facility.");
        }

        List<String> imagesToDelete = null;
        if (imagesToDeleteJson != null && !imagesToDeleteJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                imagesToDelete = objectMapper.readValue(imagesToDeleteJson, new TypeReference<List<String>>() {});
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Invalid format for imagesToDelete");
            }
        }

        try {
            FacilityDTO updatedFacility = facilityService.updateFacilityImages(id, images, imagesToDelete);
            if (updatedFacility != null) {
                return ResponseEntity.ok(updatedFacility);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save images", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/{facilityId}/isManager")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isManagerOfFacility(@PathVariable Long facilityId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        boolean isManager = facilityService.isManagerOfFacility(facilityId, user.getId());
        return ResponseEntity.ok(isManager);
    }

    @GetMapping("/isManagerOrAdmin")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isManagerOrAdmin(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = facilityService.isAdmin(user.getId());
        boolean isManager = facilityService.isManagerOfAnyFacility(user.getId());
        return ResponseEntity.ok(isAdmin || isManager);
    }
}

