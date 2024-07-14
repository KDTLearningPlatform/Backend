package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStudy(Study study);
}
