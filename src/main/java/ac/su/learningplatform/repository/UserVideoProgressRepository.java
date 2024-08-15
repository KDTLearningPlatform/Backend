package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.domain.UserVideoProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, UserVideoProgressId> {
    List<UserVideoProgress> findByUserUserIdAndVideoLectureLectureId(Long userId, Long lectureId);
    int countByUserAndVideo_LectureAndProgress(User user, Lecture lecture, float progress);
}
