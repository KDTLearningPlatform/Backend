package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VideoProgressDTO {
    private Long userId;
    private Long videoId;
    private int watchTime;
    private float progress;
    private float videoProgress;
    private float lectureProgress;
}