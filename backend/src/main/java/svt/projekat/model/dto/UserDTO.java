package svt.projekat.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import svt.projekat.model.entity.User;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank
    private String email;


    public UserDTO(User createdUser) {
        this.id = createdUser.getId();
        this.email = createdUser.getEmail();
    }
}
