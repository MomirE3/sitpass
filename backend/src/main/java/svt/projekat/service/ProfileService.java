package svt.projekat.service;

import org.springframework.web.multipart.MultipartFile;
import svt.projekat.model.dto.ProfileDTO;
import svt.projekat.model.dto.ProfileUpdateDTO;
import svt.projekat.model.dto.UserProfileUpdateResponseDTO;
import svt.projekat.model.dto.VisitHistoryDTO;
import svt.projekat.model.entity.Image;

import java.io.IOException;
import java.util.List;

public interface ProfileService {
    ProfileDTO getProfileByEmail(String email);
    UserProfileUpdateResponseDTO updateProfileByEmail(String email, ProfileDTO profileDTO);
    UserProfileUpdateResponseDTO updateProfileImageByEmail(String email, Image newImage);
    Image saveImage(MultipartFile file) throws IOException;
}
