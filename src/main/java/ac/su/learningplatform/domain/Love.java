package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Love")
@Getter
@Setter
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="love_id")  // 스터디 게시글 ID
    private Long loveId;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;


}