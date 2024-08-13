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
    // 게시글이 삭제되면 Comment 테이블의 study_id에 해당하는 댓글도 삭제되어야 합니다. (deleteStatus를 ACTIVE에서 DELETE로 변경)
    // 삭제된 게시글을 복구하는 기능을 확장할 때는, Study 테이블의 deleteDate와 Comment 테이브르이 deleteDate가 같은것을 모두 복구

    /*

    // 좋아요 수
    @Column(name="love_count", nullable = false)
    private int loveCount = 0; // 기본값은 0

    // 1. 해당컬럼은 몇명의 유저가 좋아요를 했는지를 반환하려면 성능향상을 위해 고려해야 할 컬럼입니다.
    // 2. redis 로 캐싱하여 해당 컬럼에 count 된 값을 저장합니다.
    // -- 강의 테이블 및 강의 관계테이블에서 또한 고려되어야 합니다.
    // 3. 현재는 개인 유저에게 좋아요 여부를 보여줄뿐인 (즐겨찾기 와 흡사) 기능이기에 필요가 없으며, 제외합니다.

     */

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1:N 매핑
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "study")
    private List<Love> Love;

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
