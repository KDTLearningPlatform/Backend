package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVideoProgressDTO {
    private Long videoId;
    private int lastPlaybackPosition;
}
