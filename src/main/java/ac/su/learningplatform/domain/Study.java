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
    private Long studyId;

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

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1:N 매핑
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // 엔티티 삭제시 관련 댓글 상태 변경 메서드
    public void markAsDeleted() {
        this.del = DeleteStatus.DELETED;
        this.deleteDate = LocalDateTime.now();
        if (comments != null) {
            for (Comment comment : comments) {
                comment.setDel(DeleteStatus.DELETED);
                comment.setDeleteDate(this.deleteDate);
            }
        }
    }

    // 엔티티 복구시 관련 댓글 상태 변경 메서드
    public void restore() {
        this.del = DeleteStatus.ACTIVE;
        this.deleteDate = null;
        if (comments != null) {
            for (Comment comment : comments) {
                comment.setDel(DeleteStatus.ACTIVE);
                comment.setDeleteDate(null);
            }
        }
    }
}
