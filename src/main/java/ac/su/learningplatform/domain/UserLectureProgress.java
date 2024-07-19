package ac.su.learningplatform.domain;

import com.mysql.cj.log.Log;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public UserLectureProgress(Long userId, Long lectureId) {
        this.id = new UserLectureProgressId(userId, lectureId);
    }

    public UserLectureProgress() {

    }
}