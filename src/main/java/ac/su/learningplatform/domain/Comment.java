package ac.su.learningplatform.domain;

import ac.su.learningplatform.constant.DeleteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comment")
@Getter @Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id") // 댓글 ID
    private Long commentId;

    @Column(name = "content", nullable = false) // 내용
    private String content;

    @Column(name = "create_Date", nullable = false) // 작성일자
    private LocalDateTime createDate = LocalDateTime.now(); // 기본값은 현재시간

    @Column(name = "delete_date") // 삭제일자
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "del", nullable = false) // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;
}