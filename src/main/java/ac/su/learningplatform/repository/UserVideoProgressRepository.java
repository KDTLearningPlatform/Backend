package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {

    @Query("SELECT uvp FROM UserVideoProgress uvp WHERE uvp.user.id = :userId AND uvp.video.lecture.id = :lectureId")
    List<UserVideoProgress> findByUserIdAndLectureId(Long userId, Long lectureId);

    Optional<UserVideoProgress> findByUserIdAndVideoId(Long userId, Long videoId);

    List<UserVideoProgress> findByUserIdAndVideoLectureId(Long userId, Long id);
}