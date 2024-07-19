package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User_lecture_progress")
@Getter @Setter
public class UserLectureProgress {
    @EmbeddedId
    private UserLectureProgressId id;   // 복합키

    @MapsId("userId")   // UserLectureProgressId의 userId를 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("lectureId")    // UserLectureProgressId의 lectureId를 매핑
    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name="progress", nullable = false)  // 진행도 퍼센트 0~1
    private float progress = 0;

    @Column(name="watched_count", nullable = false) // 시청 완료한 비디오 수
    private int watchedCount = 0;

    public UserLectureProgress(Long userId, Long lectureId) {

    }
}