package ac.su.learningplatform.repository;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByTagAndDelOrderByAttendanceCountDesc(String tag, DeleteStatus del);

    List<Lecture> findByTagContainingOrTitleContainingAndDelOrderByAttendanceCountDesc(String tag, String title, DeleteStatus del);

    List<Lecture> findAllByDelOrderByAttendanceCountDesc(DeleteStatus del);
}

