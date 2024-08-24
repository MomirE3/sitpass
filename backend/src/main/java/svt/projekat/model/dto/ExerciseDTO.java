package svt.projekat.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private Long userId;
    private Long facilityId;
    private LocalDateTime from;
    private LocalDateTime until;
}
