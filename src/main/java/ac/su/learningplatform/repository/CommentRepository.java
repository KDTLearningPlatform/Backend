package ac.su.learningplatform.repository;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStudy_StudyIdAndDel(Long studyId, DeleteStatus del);
}
