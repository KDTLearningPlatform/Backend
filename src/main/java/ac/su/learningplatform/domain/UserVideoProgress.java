package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "User_video_progress")
@Getter @Setter
public class UserVideoProgress {
    @EmbeddedId
    private UserVideoProgressId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("videoId")
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name="last_playback_position", nullable = false)    // 마지막 재생 시점
    private int lastPlaybackPosition = 0; // 기본값은 0

    @Column(name="progress", nullable = false)  // 진행도 퍼센트 0~1
    private float progress = 0;

    public Video getVideo() {
        return video;
    }
}