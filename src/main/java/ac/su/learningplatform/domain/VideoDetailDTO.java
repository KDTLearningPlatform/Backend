package ac.su.learningplatform.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VideoDetailDTO {
    private Long videoId;
    private String title;
    private int totalDuration;
    private int watchedDuration;
    private float progress;
}
