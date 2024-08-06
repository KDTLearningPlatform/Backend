package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VideoProgressDTO {
    private Long userId;
    private Long lectureId;
    private Long videoId;
    private String title;
    private int watchTime;
    private float progress;
}