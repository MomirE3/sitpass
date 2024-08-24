package svt.projekat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDTO {
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private LocalDate birthday;
    private String address;
    private String city;
    private String zipCode;
    private Long imageId;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
    private String imagePath;

    private List<ReviewDTO> reviews = new ArrayList<>();
    private List<FacilityDTO> managedFacilities = new ArrayList<>();
    private List<VisitHistoryDTO> visitHistory = new ArrayList<>();
}
