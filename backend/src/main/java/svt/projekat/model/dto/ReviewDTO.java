package svt.projekat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private long id;
    private long facilityId;
    private String facilityName;
    private int equipmentRating;
    private int staffRating;
    private int hygieneRating;
    private int spaceRating;
    private String comment;
    private Boolean hidden;
    private LocalDateTime createdAt;
    private int exercisesCount;
    private long rateId;
    private long userId;
    private String email;
    private List<CommentDTO> comments;

}
