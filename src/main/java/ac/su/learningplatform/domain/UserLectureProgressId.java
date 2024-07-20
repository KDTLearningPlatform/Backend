package ac.su.learningplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserLectureProgressId implements Serializable {
    @Column(name="user_id")
    private Long userId;

    @Column(name="lecture_id")
    private Long lectureId;

}