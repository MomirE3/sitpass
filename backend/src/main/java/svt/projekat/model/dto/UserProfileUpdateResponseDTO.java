package svt.projekat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileUpdateResponseDTO {
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private LocalDate birthday;
    private String address;
    private String city;
    private String zipCode;
    private Long imageId;
    private String imagePath;
}
