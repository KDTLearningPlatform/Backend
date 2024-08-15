package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class VideoProgressDTO {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long videoId;
        private int videoOrder;
        private String title;
        private String content;
        private int runningTime;
        private int lastPlaybackPosition;
    }
}
