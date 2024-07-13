package ac.su.learningplatform.domain;

import ac.su.learningplatform.constant.DeleteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Study")
@Getter @Setter
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="study_id")  // 스터디 게시글 ID
    private Long study_id;

    @Column(name="title", nullable = false)   // 제목
    private String title;

    @Column(name="field", nullable = false)   // 내용
    private String field;

    @Column(name="view_count", nullable = false)  // 조회수
    private int viewCount = 0;

    @Column(name="create_date", nullable = false) // 작성일자
    private LocalDateTime createDate = LocalDateTime.now(); // 기본값은 현재시간

    @Column(name="update_date") // 수정일자
    private LocalDateTime updateDate;

    @Column(name="delete_date") // 삭제일자
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    @Column(name="del", nullable = false)  // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE
    // 게시글이 삭제되면 Comment 테이블의 study_id에 해당하는 댓글도 삭제되어야 합니다. (deleteStatus를 ACTIVE에서 DELETE로 변경)
    // 삭제된 게시글을 복구하는 기능을 확장할 때는, Study 테이블의 deleteDate와 Comment 테이브르이 deleteDate가 같은것을 모두 복구

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1:N 매핑
    @OneToMany(mappedBy = "study")
    private List<Comment> comments;
}