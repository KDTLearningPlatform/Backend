package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.CommentDepth;
import ac.su.learningplatform.domain.CommentDepthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDepthRepository extends JpaRepository<CommentDepth, CommentDepthId> {
    List<CommentDepth> findByAncestorComment_Study_StudyIdOrderByDepthAsc(Long studyId);
    List<CommentDepth> findByAncestorComment_CommentIdOrderByDepthAsc(Long commentId);
}
