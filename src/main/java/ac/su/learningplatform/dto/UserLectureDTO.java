package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLectureDTO {
    private Long lectureId;
    private String tag;
    private String title;
    private int totalRunningTime;
}
