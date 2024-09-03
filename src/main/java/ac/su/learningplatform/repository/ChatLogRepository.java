package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
}
