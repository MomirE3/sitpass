package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.*;
import svt.projekat.model.entity.*;
import svt.projekat.repository.*;
import svt.projekat.service.EmailService;
import svt.projekat.service.ProfileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private final String imageUploadDir = "src/main/resources/static/images";

    @Override
    public ProfileDTO getProfileByEmail(String email) {
        Optional<User> userOpt = userRepository.findFirstByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setEmail(user.getEmail());
            profileDTO.setName(user.getName());
            profileDTO.setSurname(user.getSurname());
            profileDTO.setPhoneNumber(user.getPhoneNumber());
            profileDTO.setBirthday(user.getBirthday());
            profileDTO.setAddress(user.getAddress());
            profileDTO.setCity(user.getCity());
            profileDTO.setZipCode(user.getZipCode());
            profileDTO.setImageId(user.getImage() != null ? user.getImage().getId() : null);
            profileDTO.setImagePath(user.getImage() != null ? user.getImage().getPath() : null);

            List<Review> reviews = reviewRepository.findByUserId(user.getId());
            List<ReviewDTO> reviewDTOs = reviews.stream().map(review -> {
                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setFacilityId(review.getFacility().getId());
                reviewDTO.setFacilityName(review.getFacility().getName());
                reviewDTO.setEquipmentRating(review.getRate().getEquipment());
                reviewDTO.setStaffRating(review.getRate().getStaff());
                reviewDTO.setHygieneRating(review.getRate().getHygiene());
                reviewDTO.setSpaceRating(review.getRate().getSpace());
                reviewDTO.setComment(
                        review.getComments().stream().map(Comment::getText).collect(Collectors.joining(", "))
                );
                reviewDTO.setHidden(review.getHidden());
                return reviewDTO;
            }).collect(Collectors.toList());
            profileDTO.setReviews(reviewDTOs);

            List<Exercise> exercises = exerciseRepository.findByWentById(user.getId());
            List<VisitHistoryDTO> visitHistoryDTOs = exercises.stream().map(exercise -> {
                VisitHistoryDTO visitHistoryDTO = new VisitHistoryDTO();
                visitHistoryDTO.setFacilityId(exercise.getAtFacility().getId());
                visitHistoryDTO.setFacilityName(exercise.getAtFacility().getName());
                visitHistoryDTO.setFromDateTime(exercise.getFrom());
                visitHistoryDTO.setToDateTime(exercise.getUntil());
                return visitHistoryDTO;
            }).collect(Collectors.toList());
            profileDTO.setVisitHistory(visitHistoryDTOs);

            List<Facility> facilities = facilityRepository.findByManages_User(user);
            List<FacilityDTO> facilityDTOs = facilities.stream().map(facility -> {
                FacilityDTO facilityDTO = new FacilityDTO();
                facilityDTO.setId(facility.getId());
                facilityDTO.setName(facility.getName());
                facilityDTO.setCity(facility.getCity());
                facilityDTO.setAddress(facility.getAddress());
                facilityDTO.setDescription(facility.getDescription());
                facilityDTO.setTotalRating(facility.getTotalRating());
                return facilityDTO;
            }).collect(Collectors.toList());
            profileDTO.setManagedFacilities(facilityDTOs);

            return profileDTO;
        }
        return null;
    }

    @Override
    public UserProfileUpdateResponseDTO updateProfileByEmail(String email, ProfileDTO profileDTO) {
        Optional<User> userOpt = userRepository.findFirstByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            boolean passwordChanged = false;

            if (profileDTO.getOldPassword() != null && profileDTO.getNewPassword() != null) {
                if (!passwordEncoder.matches(profileDTO.getOldPassword(), user.getPassword())) {
                    throw new IllegalArgumentException("Invalid credentials or authentication failed.");
                }
                if (!profileDTO.getNewPassword().equals(profileDTO.getConfirmNewPassword())) {
                    throw new IllegalArgumentException("New password and confirm password do not match");
                }
                user.setPassword(passwordEncoder.encode(profileDTO.getNewPassword()));
                passwordChanged = true;
            }

            if (profileDTO.getPhoneNumber() != null) {
                user.setPhoneNumber(profileDTO.getPhoneNumber());
            }

            if (profileDTO.getAddress() != null) {
                user.setAddress(profileDTO.getAddress());
            }

            if (profileDTO.getName() != null) {
                user.setName(profileDTO.getName());
            }

            if (profileDTO.getSurname() != null) {
                user.setSurname(profileDTO.getSurname());
            }

            if (profileDTO.getCity() != null) {
                user.setCity(profileDTO.getCity());
            }

            if (profileDTO.getZipCode() != null) {
                user.setZipCode(profileDTO.getZipCode());
            }

            if (profileDTO.getBirthday() != null) {
                user.setBirthday(profileDTO.getBirthday());
            }

            userRepository.save(user);

            if (passwordChanged) {
                emailService.sendSimpleMessage(
                        user.getEmail(),
                        "Password Change Notification",
                        "Your password has been changed successfully."
                );
            }

            UserProfileUpdateResponseDTO responseDTO = new UserProfileUpdateResponseDTO();
            responseDTO.setEmail(user.getEmail());
            responseDTO.setName(user.getName());
            responseDTO.setSurname(user.getSurname());
            responseDTO.setPhoneNumber(user.getPhoneNumber());
            responseDTO.setBirthday(user.getBirthday());
            responseDTO.setAddress(user.getAddress());
            responseDTO.setCity(user.getCity());
            responseDTO.setZipCode(user.getZipCode());
            responseDTO.setImageId(user.getImage() != null ? user.getImage().getId() : null);
            responseDTO.setImagePath(user.getImage() != null ? user.getImage().getPath() : null);

            return responseDTO;
        }
        return null;
    }

    @Override
    public UserProfileUpdateResponseDTO updateProfileImageByEmail(String email, Image newImage) {
        Optional<User> userOpt = userRepository.findFirstByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getImage() != null) {
                deleteImage(user.getImage());
            }
            user.setImage(newImage);

            userRepository.save(user);

            UserProfileUpdateResponseDTO responseDTO = new UserProfileUpdateResponseDTO();
            responseDTO.setEmail(user.getEmail());
            responseDTO.setName(user.getName());
            responseDTO.setSurname(user.getSurname());
            responseDTO.setPhoneNumber(user.getPhoneNumber());
            responseDTO.setBirthday(user.getBirthday());
            responseDTO.setAddress(user.getAddress());
            responseDTO.setCity(user.getCity());
            responseDTO.setZipCode(user.getZipCode());
            responseDTO.setImageId(user.getImage() != null ? user.getImage().getId() : null);
            responseDTO.setImagePath(user.getImage() != null ? user.getImage().getPath() : null);

            return responseDTO;
        }
        return null;
    }

    @Override
    public Image saveImage(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(imageUploadDir, fileName);
        Files.write(filePath, file.getBytes());

        Image image = new Image();
        image.setPath("/images/" + fileName);
        return imageRepository.save(image);
    }

    private void deleteImage(Image image) {
        try {
            Files.deleteIfExists(Paths.get(image.getPath()));
            imageRepository.delete(image);
        } catch (IOException e) {
            System.out.println("Exception deleting image");
            e.printStackTrace();
        }
    }
}
