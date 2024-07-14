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

    @Column(nullable = false)
    private int watchTime = 0;

    @Column(nullable = false)
    private float progress = 0;



    public UserVideoProgress(Long userId, Long videoId) {
        this.id = new UserVideoProgressId(userId, videoId);
    }
}