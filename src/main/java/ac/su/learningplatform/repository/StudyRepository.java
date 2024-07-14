package ac.su.learningplatform.repository;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findAllByDelOrderByCreateDateDesc(DeleteStatus del);
}
