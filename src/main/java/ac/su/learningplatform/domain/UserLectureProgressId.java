package ac.su.learningplatform.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter @Setter
public class UserLectureProgressId implements Serializable {
    private String userId;
    private Long lectureId;
}