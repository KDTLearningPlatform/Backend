package ac.su.learningplatform.repository;

import ac.su.learningplatform.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByLectureIdAndDel(Long lectureId, int del);
}
