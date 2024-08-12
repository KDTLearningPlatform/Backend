package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.domain.UserVideoProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, UserVideoProgressId> {
    Optional<UserVideoProgress> findByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);
}