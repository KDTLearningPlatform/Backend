package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureDetailsDTO {
    private Long lectureId;
    //private String thumbnail;
    private String tag;
    private String title;
    private String comment;
    private Integer totalVideoCount;
    private LocalTime totalRunningTime;
    private Long userId;
    private List<VideoDTO.Response> videos;
}
