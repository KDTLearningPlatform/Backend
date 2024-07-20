package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LectureProgressDTO {
    private Long lectureId;
    private Long userId;
    private float progress;
}