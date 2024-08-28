package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMRepository extends JpaRepository<FCMToken, Long> {
    FCMToken findByFcmToken(String token);
}
