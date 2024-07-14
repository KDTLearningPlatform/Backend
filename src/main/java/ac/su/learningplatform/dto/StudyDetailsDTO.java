package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyDetailsDTO {
    private String title;
    private String field;
    private LocalDateTime createDate;
    private Long userId;
    private List<CommentDTO.Response> comments;
}
