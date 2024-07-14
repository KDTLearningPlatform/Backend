package ac.su.learningplatform.repository;
import ac.su.learningplatform.domain.UserLectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLectureProgressRepository extends JpaRepository<UserLectureProgress, Long> {
    Optional<UserLectureProgress> findByUserIdAndLectureId(Long userId, Long lectureId);
}
