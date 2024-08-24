package svt.projekat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
    private Long imageId;
    private String name;
    private String surname;
    private String zipCode;
}
