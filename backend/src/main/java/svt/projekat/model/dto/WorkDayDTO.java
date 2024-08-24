package svt.projekat.model.dto;

import lombok.*;
import svt.projekat.model.entity.DayOfWeek;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayDTO {
    private Long id;
    private LocalDate validFrom;
    private DayOfWeek day;
    private String from;
    private String until;

    public WorkDayDTO(LocalDate validFrom, DayOfWeek day, String from, String until) {
        this.validFrom = validFrom;
        this.day = day;
        this.from = from;
        this.until = until;
    }
}
