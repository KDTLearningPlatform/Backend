package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User_video_progress")
@Getter @Setter
public class UserVideoProgress {
    @EmbeddedId
    private UserVideoProgressId id;  // 복합키

    @MapsId("userId")   // UserVideoProgressId의 userId를 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("videoId")  // UserVideoProgressId의 videoId를 매핑
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name="last_playback_position", nullable = false)    // 마지막 재생 시점
    private int lastPlaybackPosition = 0; // 기본값은 0

    @Column(name="progress", nullable = false)  // 진행도 퍼센트 0~1
    private float progress = 0;

    public UserVideoProgress(Long userId, Long videoId) {
        this.id = new UserVideoProgressId(userId, videoId);
        this.user = new User();
        this.video = new Video();
        this.lastPlaybackPosition = 0;
        this.progress = 0;
    }

    public UserVideoProgress() {

    }

    public void setWatchTime(int watchTime) {
        this.lastPlaybackPosition = watchTime;
    }

    public int getWatchTime() {
        return lastPlaybackPosition;
    }
}