package svt.projekat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignManagerDTO {
    private long userId;
    private long facilityId;
    private LocalDate startDate;
    private LocalDate endDate;
}
