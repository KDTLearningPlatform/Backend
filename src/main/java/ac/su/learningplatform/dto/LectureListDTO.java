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
    private Long id;
    private String title;
    private int attendanceCount;
    private String thumbnail;
    private Long userId;
}
