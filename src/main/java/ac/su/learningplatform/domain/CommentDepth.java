package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comment_depth")
@Getter @Setter
public class CommentDepth {

    @EmbeddedId
    private CommentDepthId id;

    @MapsId("ancestorId")
    @ManyToOne
    @JoinColumn(name = "ancestor_id", nullable = false)
    private Comment ancestorComment;

    @MapsId("descendantId")
    @ManyToOne
    @JoinColumn(name="descendant_id", nullable = false)
    private Comment descendantComment;

    @Column(name="depth", nullable = false)
    private int depth;
}
