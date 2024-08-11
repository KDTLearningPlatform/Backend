package ac.su.learningplatform.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserLectureProgressDTO {
    private Long userId;
    private Long lectureId;
}
