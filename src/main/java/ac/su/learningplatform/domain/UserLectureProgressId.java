package ac.su.learningplatform.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserLectureProgressId implements Serializable {
    private Long userId;
    private Long lectureId;
}