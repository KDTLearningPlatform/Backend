package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserLectureProgress;
import ac.su.learningplatform.domain.UserLectureProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLectureProgressRepository extends JpaRepository<UserLectureProgress, UserLectureProgressId> {
    List<UserLectureProgress> findByUserUserIdAndProgressLessThan(Long userId, float progress);

    List<UserLectureProgress> findByUserUserIdAndProgressEquals(Long userId, float progress);
}
