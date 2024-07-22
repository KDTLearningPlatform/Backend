package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureListDTO {
    private Long lectureId;
    private String tag;
    private String title;
    private int totalVideoCount;
    private int attendanceCount;
    private Long userId;
}
