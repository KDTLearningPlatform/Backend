package ac.su.learningplatform.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class UserLectureProgressDTO {
    private float progress;
    private int lastPlaybackPosition;
    private boolean completed;
}
