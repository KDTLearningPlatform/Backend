package ac.su.learningplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VideoProgressDTO {
    private Long userId;
    private Long videoId;
    private int watchTime;
    private float progress;
    private float videoProgress; // 동영상 개별 진행률
    private float lectureProgress; // 강의 전체 진행률

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public int getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(int watchTime) {
        this.watchTime = watchTime;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getVideoProgress() {
        return videoProgress;
    }

    public void setVideoProgress(float videoProgress) {
        this.videoProgress = videoProgress;
    }

    public float getLectureProgress() {
        return lectureProgress;
    }

    public void setLectureProgress(float lectureProgress) {
        this.lectureProgress = lectureProgress;
    }
}