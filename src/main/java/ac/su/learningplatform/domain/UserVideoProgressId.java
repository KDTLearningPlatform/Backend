package ac.su.learningplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter @Setter
public class UserVideoProgressId implements Serializable {

    @Column(name="user_id")
    private Long userId;

    @Column(name="video_id")
    private Long videoId;
}