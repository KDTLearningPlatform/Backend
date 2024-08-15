package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByLecture(Lecture lecture);
    List<Video> findByLectureAndDeleteDateIsNull(Lecture lecture);
    List<Video> findByLecture_LectureIdAndDeleteDateIsNull(Long lectureId);
    int countByLectureAndDeleteDateIsNull(Lecture lecture);
}
