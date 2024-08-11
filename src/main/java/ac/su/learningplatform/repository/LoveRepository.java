package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Love;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoveRepository extends JpaRepository<Love, Long> {

    Love findByUser_UserIdAndStudy_StudyId(Long userId, Long studyId);

    boolean existsByUser_UserIdAndStudy_StudyId(Long userId, Long studyId);
}
