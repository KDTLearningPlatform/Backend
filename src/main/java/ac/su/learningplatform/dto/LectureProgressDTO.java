package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LectureProgressDTO {
    private Long lectureId;
    private Long userId;
    private float progress;
    private String tag;
    private String title;
    private String totalDuration;
    private int watchedVideos;
    private int totalVideos;

    // 기본 생성자
    public LectureProgressDTO() {}

    // 모든 필드를 매개변수로 받는 생성자
    public LectureProgressDTO(Long lectureId, Long userId,String tag, String title, String totalDuration, int watchedVideos, int totalVideos) {
        this.lectureId = lectureId;
        this.userId = userId;
        this.tag = tag;
        this.title = title;
        this.totalDuration = totalDuration;
        this.watchedVideos = watchedVideos;
        this.totalVideos = totalVideos;
    }
}