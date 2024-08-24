package svt.projekat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class VisitHistoryDTO {
    private Long facilityId;
    private String facilityName;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
}
