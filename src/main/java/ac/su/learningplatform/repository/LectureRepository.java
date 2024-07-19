package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByIdAndDel(Long id, int del);
    List<Lecture> findByUserAndAttendanceCountGreaterThan(User user, int attendanceCount);
}