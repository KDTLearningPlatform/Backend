package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyLectureDetailsDTO {
    private String lectureTitle;
    private float lectureProgress;
    private List<VideoProgressDTO> videos;

    // 기존 VideoProgressDTO 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoProgressDTO {
        private Long videoId;
        private String title;
        private int runningTime;
        private float progress;
    }
}

