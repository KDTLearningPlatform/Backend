package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LectureCompletedDto {
    private Long id;
    private String title;
    //private String thumbnail;
}