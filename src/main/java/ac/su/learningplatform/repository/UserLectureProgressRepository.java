package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserLectureProgress;
import ac.su.learningplatform.domain.UserLectureProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLectureProgressRepository extends JpaRepository<UserLectureProgress, UserLectureProgressId> {
    Optional<UserLectureProgress> findById_UserIdAndId_LectureId(Long userId, Long lectureId);
}
