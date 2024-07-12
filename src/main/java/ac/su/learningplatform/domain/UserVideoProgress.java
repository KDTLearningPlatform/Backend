package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name="watch_time", nullable = false)
    private int watchTime = 0;

    @Column(name="progress", nullable = false)
    private float progress = 0;
}