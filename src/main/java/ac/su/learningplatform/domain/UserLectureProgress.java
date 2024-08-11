package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User_lecture_progress")
@Getter @Setter
public class UserLectureProgress {
    @EmbeddedId
    private UserLectureProgressId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("lectureId")
    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false)
    private float progress = 0;

    @Column(name="watched_count", nullable = false) // 시청 완료한 비디오 수
    private int watchedCount = 0;
}