package ac.su.learningplatform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LectureCompletedDTO {
    private Long lectureId;
    private String title;
    private String tag;
    private int totalDuration;

//    public LectureCompletedDto(Long lectureId, String title, String tag, int totalDuration) {
//        this.lectureId = lectureId;
//        this.title = title;
//        this.tag = tag;
//        this.totalDuration = totalDuration;
//    }
}