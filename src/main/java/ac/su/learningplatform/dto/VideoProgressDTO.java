package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VideoProgressDTO {
    private Long videoId;
    private int watchTime;
    private float progress;
}