package ac.su.learningplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLectureProgressId implements Serializable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "lecture_id")
    private Long lectureId;
}