package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.ProfileDTO;
import svt.projekat.model.dto.UserProfileUpdateResponseDTO;
import svt.projekat.model.entity.Image;
import svt.projekat.service.ProfileService;

import java.io.IOException;

@RestController
@RequestMapping("api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = getEmailFromPrincipal(userDetails);
        ProfileDTO profileDTO = profileService.getProfileByEmail(email);
        if (profileDTO != null) {
            return ResponseEntity.ok(profileDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(consumes = "application/json")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody ProfileDTO profileDTO) {
        String email = getEmailFromPrincipal(userDetails);
        try {
            if (profileDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No profile data to update.");
            }
            UserProfileUpdateResponseDTO updatedProfile = profileService.updateProfileByEmail(email, profileDTO);
            if (updatedProfile != null) {
                return ResponseEntity.ok(updatedProfile);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfilePicture(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestPart(value = "file") MultipartFile file) {
        String email = getEmailFromPrincipal(userDetails);
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file to upload.");
            }
            Image newImage = profileService.saveImage(file);
            UserProfileUpdateResponseDTO updatedProfile = profileService.updateProfileImageByEmail(email, newImage);
            if (updatedProfile != null) {
                return ResponseEntity.ok(updatedProfile);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    private String getEmailFromPrincipal(UserDetails userDetails) {
        return userDetails.getUsername();
    }
}

