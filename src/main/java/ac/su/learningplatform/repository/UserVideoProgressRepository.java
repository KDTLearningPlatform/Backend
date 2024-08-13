package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {
    Optional<UserVideoProgress> findByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);

    List<UserVideoProgress> findByUser_UserIdAndVideo_Lecture_LectureId(Long userId, Long lectureId);
}