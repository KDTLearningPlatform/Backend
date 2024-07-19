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
public class UserVideoProgressId implements Serializable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "video_id")
    private Long videoId;

    public UserVideoProgressId(Long userId, Long videoId) {
    }
}