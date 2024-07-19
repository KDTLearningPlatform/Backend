package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {

    List<UserVideoProgress> findByUserUserId(Long userId);

    List<UserVideoProgress> findByVideoVideoId(Long videoId);

    Optional<UserVideoProgress> findByUserUserIdAndVideoVideoId(Long userId, Long videoId);

    List<UserVideoProgress> findByUserIdAndLectureId(Long userId, Long lectureId);
}