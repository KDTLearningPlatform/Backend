package ac.su.learningplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentDepthId implements Serializable {
    @Column(name="ancestor_id")
    private Long ancestorId;

    @Column(name="descendant_id")
    private Long descendantId;
}
