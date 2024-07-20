package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    //생성자 추가
    public UserLectureProgress(UserLectureProgressId id, User user, Lecture lecture, float progress, int watchedCount) {
        this.id = id;
        this.user = user;
        this.lecture = lecture;
        this.progress = progress;
        this.watchedCount = watchedCount;
    }

    //생성자 추가
    public UserLectureProgress(Long userId, Long lectureId) {
        this.id = new UserLectureProgressId(userId, lectureId);
        this.user = new User();
        this.lecture = new Lecture();
        this.progress = 0;
        this.watchedCount = 0;
    }

}