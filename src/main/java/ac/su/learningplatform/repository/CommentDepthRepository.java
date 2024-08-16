package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.CommentDepth;
import ac.su.learningplatform.domain.CommentDepthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentDepthRepository extends JpaRepository<CommentDepth, CommentDepthId> {
    // descendantComment로 CommentDepth를 하나만 반환하도록 수정
    CommentDepth findFirstByDescendantComment(Comment descendantComment);

    // 특정 댓글 ID에 대한 depth를 조회합니다.
    Optional<CommentDepth> findFirstByDescendantComment_CommentId(Long commentId);

    // findFirstByDescendantComment_CommentId 의 @Query 버전
    // 사용안함
//    @Query("SELECT cd.depth FROM CommentDepth cd WHERE cd.descendantComment.commentId = :commentId")
//    Integer findDepthByDescendantComment_CommentId(@Param("commentId") Long commentId);


    @Query("SELECT cd.descendantComment.commentId FROM CommentDepth cd WHERE cd.ancestorComment.commentId = :ancestorId")
    List<Long> findDescendantIdsByAncestorComment_CommentId(@Param("ancestorId") Long ancestorId);
}
