package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    public LectureProgressDTO(String tag, String title, String totalDuration, int watchedVideos, int totalVideos) {
        this.tag = tag;
        this.title = title;
        this.totalDuration = totalDuration;
        this.watchedVideos = watchedVideos;
        this.totalVideos = totalVideos;
    }

    // Getters and Setters
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getWatchedVideos() {
        return watchedVideos;
    }

    public void setWatchedVideos(int watchedVideos) {
        this.watchedVideos = watchedVideos;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }
}