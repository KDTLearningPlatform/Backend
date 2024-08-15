package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLectureDTO {
    private Long lectureId;
    private String tag;
    private String title;
    private int totalRunningTime;
    private float progress;
    private int watchedCount;
    private int totalVideoCount;

    public UserLectureDTO(Long lectureId, String tag, String title, int totalRunningTime, int watchedCount, int totalVideoCount) {
        this.lectureId = lectureId;
        this.tag = tag;
        this.title = title;
        this.totalRunningTime = totalRunningTime;
        this.watchedCount = watchedCount;
        this.totalVideoCount = totalVideoCount;
        this.progress = (float) watchedCount / totalVideoCount;
    }
}
