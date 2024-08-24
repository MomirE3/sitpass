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
public class CommentDTO {
    private long id;
    private String text;
    private long parentCommentId;
    private long reviewId;
    private long userId;
    private String email;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies;
}
