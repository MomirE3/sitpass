package svt.projekat.service.implementation;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.FacilityCreationDTO;
import svt.projekat.model.dto.FacilityDTO;
import svt.projekat.model.dto.WorkDayDTO;
import svt.projekat.model.entity.*;
import svt.projekat.repository.*;
import svt.projekat.service.FacilityService;
import svt.projekat.utils.TimeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private WorkDayRepository workDayRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ManagesRepository managesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private final String imageUploadDir = "src/main/resources/static/images";

    private static final Logger logger = LoggerFactory.getLogger(FacilityServiceImpl.class);


    @Override
    public List<FacilityDTO> findAll() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .map(facility -> new FacilityDTO(
                        facility.getId(),
                        facility.getName(),
                        facility.getCity(),
                        facility.getAddress(),
                        facility.getDescription(),
                        facility.getTotalRating(),
                        facility.getDisciplines() != null ? facility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                        facility.getWorkDays() != null ? facility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                                workDay.getId(),
                                workDay.getValidFrom(),
                                workDay.getDay(),
                                TimeUtils.localTimeToString(workDay.getFrom()),
                                TimeUtils.localTimeToString(workDay.getUntil())
                        )).collect(Collectors.toList()) : Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public FacilityDTO findById(Long id) {
        return facilityRepository.findById(id)
                .map(facility -> new FacilityDTO(
                        facility.getId(),
                        facility.getName(),
                        facility.getCity(),
                        facility.getAddress(),
                        facility.getDescription(),
                        facility.getTotalRating(),
                        facility.getDisciplines() != null ? facility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                        facility.getWorkDays() != null ? facility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                                workDay.getId(),
                                workDay.getValidFrom(),
                                workDay.getDay(),
                                TimeUtils.localTimeToString(workDay.getFrom()),
                                TimeUtils.localTimeToString(workDay.getUntil())
                        )).collect(Collectors.toList()) : Collections.emptyList(),
                        facility.getImages() != null ? facility.getImages().stream().map(image -> getImageUrl(image.getPath())).collect(Collectors.toList()) : Collections.emptyList() // Ensure absolute URLs
                ))
                .orElse(null);
    }
    private String getImageUrl(String imagePath) {
        return "http://localhost:8080" + imagePath;
    }

    @Override
    public List<FacilityDTO> findFacilitiesManagedByUser(Long userId) {
        List<Facility> facilities = facilityRepository.findByManagerId(userId);
        return facilities.stream()
                .map(facility -> new FacilityDTO(
                        facility.getId(),
                        facility.getName(),
                        facility.getCity(),
                        facility.getAddress(),
                        facility.getDescription(),
                        facility.getTotalRating(),
                        facility.getDisciplines() != null ? facility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                        facility.getWorkDays() != null ? facility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                                workDay.getId(),
                                workDay.getValidFrom(),
                                workDay.getDay(),
                                TimeUtils.localTimeToString(workDay.getFrom()),
                                TimeUtils.localTimeToString(workDay.getUntil())
                        )).collect(Collectors.toList()) : Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isManagerOfAnyFacility(Long userId) {
        return managesRepository.existsByUserId(userId);
    }

    @Override
    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user instanceof Administrator)
                .orElse(false);
    }

    @Override
    @Transactional
    public FacilityDTO createFacilityWithImages(FacilityCreationDTO request, List<MultipartFile> images) throws IOException {
        if (images != null && images.size() > 0 && images.size() < 2) {
            throw new IllegalArgumentException("At least two images are required if any images are provided.");
        }

        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setDescription(request.getDescription());
        facility.setAddress(request.getAddress());
        facility.setCity(request.getCity());
        facility.setCreatedAt(LocalDate.now());
        facility.setActive(request.isActive());
        facility.setTotalRating(0.0);

        Facility savedFacility = facilityRepository.save(facility);

        if (request.getDisciplines() != null) {
            for (String disciplineName : request.getDisciplines()) {
                Discipline discipline = new Discipline();
                discipline.setName(disciplineName);
                discipline.setByFacility(savedFacility);
                disciplineRepository.save(discipline);
            }
        }

        if (request.getWorkDays() != null) {
            for (WorkDayDTO workDayDTO : request.getWorkDays()) {
                WorkDay workDay = new WorkDay();
                workDay.setValidFrom(workDayDTO.getValidFrom() != null ? workDayDTO.getValidFrom() : LocalDate.now());
                workDay.setDay(workDayDTO.getDay());
                workDay.setFrom(TimeUtils.stringToLocalTime(workDayDTO.getFrom()));
                workDay.setUntil(TimeUtils.stringToLocalTime(workDayDTO.getUntil()));
                workDay.setFacility(savedFacility);
                workDayRepository.save(workDay);
            }
        }

        if (images != null) {
            for (MultipartFile imageFile : images) {
                String imagePath = saveImage(imageFile);
                Image image = new Image();
                image.setPath(imagePath);
                image.setBelongsToFacility(savedFacility);
                imageRepository.save(image);
            }
        }

        return new FacilityDTO(
                savedFacility.getId(),
                savedFacility.getName(),
                savedFacility.getCity(),
                savedFacility.getAddress(),
                savedFacility.getDescription(),
                savedFacility.getTotalRating(),
                savedFacility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()),
                savedFacility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                        workDay.getValidFrom(),
                        workDay.getDay(),
                        TimeUtils.localTimeToString(workDay.getFrom()),
                        TimeUtils.localTimeToString(workDay.getUntil())
                )).collect(Collectors.toList())
        );
    }

    @Override
    public List<Facility> filterFacilities(List<String> disciplineNames, Double minRating, Double maxRating,
                                           LocalTime fromTime, LocalTime untilTime, List<String> cities) {
        return facilityRepository.filterFacilities(cities, disciplineNames, minRating, maxRating, fromTime, untilTime);
    }


    @Override
    public List<String> getAllUniqueCities() {
        return facilityRepository.findAllUniqueCities();
    }

    @Override
    public List<FacilityDTO> findEligibleFacilitiesForReview(Long userId) {
        List<Facility> visitedFacilities = exerciseRepository.findDistinctFacilitiesByUserIdAndUntilDateTimeBefore(userId, LocalDateTime.now());
        List<Facility> reviewedFacilities = reviewRepository.findByUser(new User(userId)).stream()
                .map(Review::getFacility)
                .collect(Collectors.toList());

        visitedFacilities.removeAll(reviewedFacilities);

        return visitedFacilities.stream()
                .map(facility -> new FacilityDTO(
                        facility.getId(),
                        facility.getName(),
                        facility.getCity(),
                        facility.getAddress(),
                        facility.getDescription(),
                        facility.getTotalRating(),
                        facility.getDisciplines() != null ? facility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                        facility.getWorkDays() != null ? facility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                                workDay.getId(),
                                workDay.getValidFrom(),
                                workDay.getDay(),
                                TimeUtils.localTimeToString(workDay.getFrom()),
                                TimeUtils.localTimeToString(workDay.getUntil())
                        )).collect(Collectors.toList()) : Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Facility> findFacilitiesInUserCity(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        return facilityRepository.findByCity(user.getCity());
    }

    @Override
    public List<Facility> findMostPopularFacilities() {
        return facilityRepository.findTop5ByOrderByTotalRatingDesc();
    }

    @Override
    public List<Facility> findFacilitiesUserHasVisited(Long userId) {
        return exerciseRepository.findDistinctFacilitiesByUserIdAndUntilDateTimeBefore(userId, LocalDateTime.now());
    }

    @Override
    public List<Facility> findNewFacilitiesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        return facilityRepository.findNewFacilitiesForUser(userId, user.getCity());
    }

    @Override
    public List<Facility> findNewFacilitiesForUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Pageable pageable = PageRequest.of(page, size);
        return facilityRepository.findNewFacilitiesForUser(userId, pageable).getContent();
    }


    @Override
    public List<Facility> getActiveFacilities() {
        return facilityRepository.findByActiveTrue();
    }

    @Override
    public List<Facility> getInactiveFacilities() {
        return facilityRepository.findByActiveFalse();
    }

    @Override
    @Transactional
    public FacilityDTO updateFacility(Long id, FacilityDTO facilityDTO) {
        Optional<Facility> facilityOpt = facilityRepository.findById(id);
        if (facilityOpt.isPresent()) {
            Facility facility = facilityOpt.get();

            if (facilityDTO.getName() != null) {
                facility.setName(facilityDTO.getName());
            }
            if (facilityDTO.getCity() != null) {
                facility.setCity(facilityDTO.getCity());
            }
            if (facilityDTO.getAddress() != null) {
                facility.setAddress(facilityDTO.getAddress());
            }
            if (facilityDTO.getDescription() != null) {
                facility.setDescription(facilityDTO.getDescription());
            }

            if (facilityDTO.getDisciplines() != null) {
                List<String> newDisciplineNames = facilityDTO.getDisciplines();
                disciplineRepository.deleteByByFacilityAndNameNotIn(facility, newDisciplineNames);

                List<Discipline> existingDisciplines = disciplineRepository.findByByFacility(facility);
                for (String disciplineName : newDisciplineNames) {
                    if (existingDisciplines.stream().noneMatch(discipline -> discipline.getName().equals(disciplineName))) {
                        Discipline newDiscipline = new Discipline();
                        newDiscipline.setName(disciplineName);
                        newDiscipline.setByFacility(facility);
                        disciplineRepository.save(newDiscipline);
                    }
                }
            }

            if (facilityDTO.getWorkDays() != null) {
                List<Long> newWorkDayIds = facilityDTO.getWorkDays().stream()
                        .map(WorkDayDTO::getId)
                        .filter(workDayId -> workDayId != null)
                        .collect(Collectors.toList());

                workDayRepository.deleteByFacilityAndIdNotIn(facility, newWorkDayIds);

                List<WorkDay> existingWorkDays = workDayRepository.findByFacility(facility);
                for (WorkDayDTO workDayDTO : facilityDTO.getWorkDays()) {
                    if (workDayDTO.getId() != null) {
                        Optional<WorkDay> optionalWorkDay = existingWorkDays.stream()
                                .filter(workDay -> workDay.getId() == (workDayDTO.getId()))
                                .findFirst();

                        if (optionalWorkDay.isPresent()) {
                            WorkDay workDay = optionalWorkDay.get();
                            workDay.setValidFrom(LocalDate.now());
                            workDay.setDay(workDayDTO.getDay());
                            workDay.setFrom(TimeUtils.stringToLocalTime(workDayDTO.getFrom()));
                            workDay.setUntil(TimeUtils.stringToLocalTime(workDayDTO.getUntil()));
                            workDayRepository.save(workDay);
                        }
                    } else {
                        WorkDay newWorkDay = new WorkDay();
                        newWorkDay.setValidFrom(LocalDate.now());
                        newWorkDay.setDay(workDayDTO.getDay());
                        newWorkDay.setFrom(TimeUtils.stringToLocalTime(workDayDTO.getFrom()));
                        newWorkDay.setUntil(TimeUtils.stringToLocalTime(workDayDTO.getUntil()));
                        newWorkDay.setFacility(facility);
                        workDayRepository.save(newWorkDay);
                    }
                }
            }

            Facility updatedFacility = facilityRepository.save(facility);
            return new FacilityDTO(
                    updatedFacility.getId(),
                    updatedFacility.getName(),
                    updatedFacility.getCity(),
                    updatedFacility.getAddress(),
                    updatedFacility.getDescription(),
                    updatedFacility.getTotalRating(),
                    updatedFacility.getDisciplines() != null ? updatedFacility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                    updatedFacility.getWorkDays() != null ? updatedFacility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                            workDay.getId(),
                            workDay.getValidFrom(),
                            workDay.getDay(),
                            TimeUtils.localTimeToString(workDay.getFrom()),
                            TimeUtils.localTimeToString(workDay.getUntil())
                    )).collect(Collectors.toList()) : Collections.emptyList()
            );
        }
        return null;
    }

    @Override
    @Transactional
    public FacilityDTO updateFacilityImages(Long id, List<MultipartFile> images, List<String> imagesToDelete) throws IOException {
        Optional<Facility> facilityOpt = facilityRepository.findById(id);
        if (facilityOpt.isPresent()) {
            Facility facility = facilityOpt.get();

            List<Image> existingImages = new ArrayList<>(facility.getImages());
            int initialCount = existingImages.size();
            int deleteCount = imagesToDelete != null ? imagesToDelete.size() : 0;
            int addCount = images != null ? images.size() : 0;
            int finalCount = initialCount - deleteCount + addCount;

            if (finalCount != 0 && finalCount < 2) {
                throw new IllegalArgumentException("At least two images are required if any images are provided.");
            }

            if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                for (String imagePath : imagesToDelete) {
                    imageRepository.findByPath(imagePath).ifPresent(image -> {
                        try {
                            Path path = Paths.get("src/main/resources/static", image.getPath());
                            boolean deleted = Files.deleteIfExists(path);
                            if (deleted) {
                                logger.info("Successfully deleted image file: {}", path);
                            } else {
                                logger.warn("Image file not found for deletion: {}", path);
                            }
                        } catch (IOException e) {
                            logger.error("Error deleting image file: {}", image.getPath(), e);
                        }
                        logger.info("Deleting image from database with id: {}", image.getId());
                        imageRepository.deleteById(image.getId());
                    });
                }
                imageRepository.flush();
            }

            if (images != null && !images.isEmpty()) {
                List<String> existingImageFileNames = existingImages.stream()
                        .map(image -> Paths.get(image.getPath()).getFileName().toString())
                        .collect(Collectors.toList());

                for (MultipartFile imageFile : images) {
                    String newImageFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                    if (!existingImageFileNames.contains(newImageFileName)) {
                        String imagePath = saveImageWithFileName(imageFile, newImageFileName);
                        Image image = new Image();
                        image.setPath(imagePath);
                        image.setBelongsToFacility(facility);
                        imageRepository.save(image);
                        logger.info("Added new image to database: {}", imagePath);
                    }
                }
                imageRepository.flush();
            }

            Facility updatedFacility = facilityRepository.save(facility);
            return new FacilityDTO(
                    updatedFacility.getId(),
                    updatedFacility.getName(),
                    updatedFacility.getCity(),
                    updatedFacility.getAddress(),
                    updatedFacility.getDescription(),
                    updatedFacility.getTotalRating(),
                    updatedFacility.getDisciplines() != null ? updatedFacility.getDisciplines().stream().map(Discipline::getName).collect(Collectors.toList()) : Collections.emptyList(),
                    updatedFacility.getWorkDays() != null ? updatedFacility.getWorkDays().stream().map(workDay -> new WorkDayDTO(
                            workDay.getId(),
                            workDay.getValidFrom(),
                            workDay.getDay(),
                            TimeUtils.localTimeToString(workDay.getFrom()),
                            TimeUtils.localTimeToString(workDay.getUntil())
                    )).collect(Collectors.toList()) : Collections.emptyList(),
                    updatedFacility.getImages() != null ? updatedFacility.getImages().stream().map(image -> "http://localhost:8080" + image.getPath()).collect(Collectors.toList()) : Collections.emptyList()
            );
        }
        return null;
    }

    private String saveImageWithFileName(MultipartFile imageFile, String fileName) throws IOException {
        Path filePath = Paths.get(imageUploadDir, fileName);
        Files.write(filePath, imageFile.getBytes());
        return "/images/" + fileName;
    }


    @Override
    public boolean isManagerOfFacility(Long facilityId, Long userId) {
        return managesRepository.existsByFacilityIdAndUserId(facilityId, userId);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(imageUploadDir, fileName);
        Files.write(filePath, imageFile.getBytes());
        return "/images/" + fileName;
    }
}
