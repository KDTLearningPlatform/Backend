package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.CommentDepth;
import ac.su.learningplatform.domain.CommentDepthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentDepthRepository extends JpaRepository<CommentDepth, CommentDepthId> {
    // descendantComment로 CommentDepth를 하나만 반환하도록 수정
    CommentDepth findFirstByDescendantComment(Comment descendantComment);
}
