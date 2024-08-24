package svt.projekat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCreationDTO {
    private String name;
    private String description;
    private String address;
    private String city;
    private LocalDate createdAt;
    private boolean active;
    private List<String> disciplines;
    private List<WorkDayDTO> workDays;
}
